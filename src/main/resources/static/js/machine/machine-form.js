/**
 * 대문자 변환
 * @param str
 * @returns {string}
 */
function toUpperCase(str) {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

const test = true;

// form validation 확인
const validators = {
    isValidBuyDate: (date) => /^\d{4}-\d{2}-\d{2}$/.test(date)
};

// 수정 가능한 필드 요소 가져오기
const getEditableFields = () => {
    const machineName = document.querySelector("input[name='machineName']");
    const manufacturer = document.querySelector("input[name='manufacturer']");
    const buyDate = document.querySelector("input[name='buyDate']");
    return [machineName, manufacturer, buyDate];
};

// 폼 필드를 수정 가능하게 설정
const setFormAccess = (inputList) => {
    inputList.forEach((input) => {
        input.disabled = false;
    });
};

const init = async () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    const closeBtn = form.querySelector("button.btn-secondary");
    let isEditMode = false; // 수정모드
    let processData = {}; // 공정 데이터 저장

    const btnModify = form.querySelector("button.modBtn") ? form.querySelector("button.modBtn") : undefined;


    try {
        // 부모창에서 데이터 받아오기
        window.addEventListener('message', function(event) {
            const data = event.data;
            if (data?.source === 'react-devtools-content-script') return;

            // 받아온 데이터 로깅
            console.log("받은 데이터:", data);

            // 받아온 데이터 저장
            processData = data;

            // 폼에 데이터 채우기
            form.querySelector("input[name='machineName']").value = data.machineName || "";
            form.querySelector("input[name='machineId']").value = data.machineId || "";
            form.querySelector("input[name='manufacturer']").value = data.manufacturer || "";
            form.querySelector("input[name='buyDate']").value = data.buyDate || "";
            form.querySelector("input[name='processId']").value = data.processId || "";
            form.querySelector("input[name='processName']").value = data.processName || "";
        });

        // 수정 버튼 클릭 이벤트
        const clickEvent = () => {
            if(!btnModify) {
                return;
            }

            btnModify.addEventListener("click", () => {
                if (!isEditMode) {
                    // 수정모드 아닌 경우 수정 모드로 변경
                    setFormAccess(getEditableFields());
                    btnModify.textContent = "저장";
                    isEditMode = true;
                } else {
                    // 수정모드인 경우 - 유효성 검사 후 모달 표시
                    const buyDate = document.querySelector("input[name='buyDate']").value;

                    // 구입 일자 유효성 검사
                    if(!validators.isValidBuyDate(buyDate)) {
                        return alert("구입 일자 형식이 올바르지 않습니다. (YYYY-MM-DD 형식이어야 합니다.)");
                    }

                    confirmModal.show();
                }
            });
        }

        clickEvent();

        // 닫기 버튼 클릭 이벤트
        closeBtn.addEventListener('click', () => {
            window.close();
        });

        // 확인 모달 - 저장 버튼
        confirmEditBtn.addEventListener("click", () => {
            saveData().then(res => {
                // 수정모드 종료
                getEditableFields().forEach(field => field.disabled = true);
                btnModify.textContent = "수정";

                isEditMode = false;

                confirmModal.hide();
                document.querySelector(".alertModal .modal-body").textContent = res.message || "저장이 완료되었습니다!";
                alertModal.show();
            }).catch(err => {
                alert("저장 중 오류가 발생했습니다: " + err.message);
                confirmModal.hide();
            });
        });

        // 알림 모달 - 확인 버튼
        alertBtn.addEventListener("click", () => {
            alertModal.hide();

            // 부모 창의 그리드 리프레시
            if (window.opener && !window.opener.closed) {
                window.opener.postMessage({ type: "REFRESH_MACHINES" }, "*");
            }

            window.close();
        });

        // 데이터 저장 함수
        async function saveData() {
            const data = {
                machineId: processData.machineId,
                machineName: toUpperCase(document.querySelector("input[name='machineName']").value),
                manufacturer: document.querySelector("input[name='manufacturer']").value,
                buyDate: document.querySelector("input[name='buyDate']").value
            };

            try {
                const res = await fetch(`/api/machine`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data),
                });
                return res.json();
            } catch (e) {
                console.error(e);
                throw e;
            }
        }
    } catch (error) {
        console.error('에러: ', error);
    }
};

window.onload = async () => {
    await init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};