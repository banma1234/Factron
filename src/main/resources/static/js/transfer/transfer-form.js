const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.saveBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let data = {}; // 저장 데이터

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 초기 값 세팅
        form.querySelector("input[name='requesterId']").value = data.empId || "";
    });

    // 발령 구분 선택
    form.querySelector("select[name='trsTypeCode']").addEventListener('change', (e) => {
        if (e.target.value === 'TRS001') {
            // 승진 - 직급 display
            document.querySelector("select[name='currDeptCode']").value = '';
            document.querySelector("select[name='currPositionCode']").value = '';
            form.querySelector(".trsDept").classList.add('d-none');
            form.querySelector(".trsPos").classList.remove('d-none');
        } else {
            // 전보 - 부서 dispay
            document.querySelector("select[name='currDeptCode']").value = '';
            document.querySelector("select[name='currPositionCode']").value = '';
            form.querySelector(".trsDept").classList.remove('d-none');
            form.querySelector(".trsPos").classList.add('d-none');
        }
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = form.querySelector("input[name='empId']").value.trim();
        const empName = form.querySelector("input[name='empName']").value.trim();
        const prevDeptCode = form.querySelector("select[name='deptCode']").value;
        const trsTypeCode = form.querySelector("select[name='trsTypeCode']").value;
        const currDeptCode = form.querySelector("select[name='currDeptCode']").value;
        const currPositionCode = form.querySelector("select[name='currPositionCode']").value;

        // validation
        if (!empId) {
            alert("발령자를 선택해주세요.");
            return;
        }
        if (!trsTypeCode) {
            alert("발령 구분을 선택해주세요.");
            return;
        }
        if (trsTypeCode === 'TRS001' && !currPositionCode) { // 승진
            alert("발령 직급을 선택해주세요.");
            return;
        }
        if (trsTypeCode === 'TRS002' && !currDeptCode) { // 전보
            alert("발령 부서를 선택해주세요.");
            return;
        }

        // fetch data
        data = {
            requesterId: form.querySelector("input[name='requesterId']").value.trim(),
            empId,
            trsTypeCode,
            prevDeptCode,
            currDeptCode,
            currPositionCode,
        };

        document.querySelector(".confirmModal .modal-body").innerHTML =
            `${empName} 님을 발령하시겠습니까?`;
        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        saveData().then(res => {
            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        window.close();
    });

    // 저장
    async function saveData() {
        try {
            const res = await fetch(`/api/trans`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    if (window.opener && !window.opener.closed) {
        // 발령구분 세팅
        window.opener.getSysCodeList("TRS").then(res => {
            const selectElement = document.querySelector("select[name='trsTypeCode']");

            // 하드코딩
            const data = [
                {
                    "detailCode": "TRS001",
                    "name": "승진"
                },
                {
                    "detailCode": "TRS002",
                    "name": "전보"
                },
            ];

            // for(const trsType of res.data) {
            for(const trsType of data) {
                const optionElement = document.createElement("option");
                optionElement.value = trsType.detailCode;  // 코드
                optionElement.textContent = trsType.name;  // 이름

                selectElement.appendChild(optionElement);
            }
        }).catch(e => {
            console.error(e);
        });

        // 직급 세팅
        window.opener.getSysCodeList("DEP").then(res => {
            const selectElPrev = document.querySelector("select[name='deptCode']");
            const selectElCurr = document.querySelector("select[name='currDeptCode']");

            // 하드코딩
            const data = [
                {
                    "detailCode": "DEP001",
                    "name": "인사부"
                },
                {
                    "detailCode": "DEP002",
                    "name": "개발부"
                },
                {
                    "detailCode": "DEP003",
                    "name": "영업부"
                },
                {
                    "detailCode": "DEP004",
                    "name": "생산부"
                }
            ];

            // for(const dept of res.data) {
            for(const dept of data) {
                // prev
                const optionElPrev = document.createElement("option");
                optionElPrev.value = dept.detailCode;
                optionElPrev.textContent = dept.name;
                selectElPrev.appendChild(optionElPrev);
                // curr
                const optionElCurr = document.createElement("option");
                optionElCurr.value = dept.detailCode;
                optionElCurr.textContent = dept.name;
                selectElCurr.appendChild(optionElCurr);
            }
        }).catch(e => {
            console.error(e);
        });

        // 부서 세팅
        window.opener.getSysCodeList("POS").then(res => {
            const selectElPrev = document.querySelector("select[name='positionCode']");
            const selectElCurr = document.querySelector("select[name='currPositionCode']");

            // 하드코딩
            const data = [
                {
                    "detailCode": "POS001",
                    "name": "사원"
                },
                {
                    "detailCode": "POS002",
                    "name": "주임"
                },
                {
                    "detailCode": "POS003",
                    "name": "대리"
                }
            ];

            // for(const pos of res.data) {
            for(const pos of data) {
                // prev
                const optionElPrev = document.createElement("option");
                optionElPrev.value = pos.detailCode;
                optionElPrev.textContent = pos.name;
                selectElPrev.appendChild(optionElPrev);
                // curr
                const optionElCurr = document.createElement("option");
                optionElCurr.value = pos.detailCode;
                optionElCurr.textContent = pos.name;
                selectElCurr.appendChild(optionElCurr);
            }
        }).catch(e => {
            console.error(e);
        });
    }
};

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};