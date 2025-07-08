/**
 * 새로운 row 추가시 셀에 입력될 데이터 기본값
 * */
const NEW_ROW = {
    id: undefined,
    name: '',
    business_number: undefined,
    address: '',
    contact: '',
    ceo: '',
    contact_manager: '',
    remark: ''
}

const init = () => {

    /**
     * 사업자등록번호 입력 모달창
     * */
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    /**
     * 사업자등록번호 입력 완료 버튼
     * */
    const confirmBtn = document.querySelector("button[name='confirmBtn']");
    /**
     * 사업자등록번호 입력하는 인풋
     * */
    const businessNumberInput = document.querySelector("input[name='srchBusinessNumber']");
    /**
     * 사업자등록번호 추가/수정할 대상 셀의 좌표값(rowKey)
     * */
    let selectedRowDataKey = undefined;
    /**
     * alert 띄우는 모달창
     * */
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    /**
     * alert 모달 종료버튼
     * */
    const alertBtn = document.querySelector("button[name='alertBtn']");
    /**
     * alert 모달 내부 텍스트
     * */
    const alertModalText = document.querySelector(".alert-modal-text");

    /**
     * `client` 테이블 초기화
     * */
    const clientGrid = initGrid(
        document.getElementById('grid_client'),
        400,
        [
            {
                header: 'id',
                name: 'id',
                align: "center",
            },
            {
                header: '거래처명',
                name: 'name',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
            {
                header: '사업자등록번호',
                name: 'business_number',
                align: 'center',
            },
            {
                header: '주소',
                name: 'address',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
            {
                header: '연락처',
                name: 'contact',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
            {
                header: '대표자',
                name: 'ceo',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
            {
                header: '담당자',
                name: 'contact_manager',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
            {
                header: '비고',
                name: 'remark',
                align: 'center',
                editor: ['ATH003', 'ATH005'].includes(window.user.authCode) ? 'text' : false
            },
        ]
    );

    /**
     * `client` 요청 api
     * @param {string} name 거래처명
     * @return JSON
     * */
    const getClient = async (name) => {
        try {
            const res = await fetch(`/api/client?name=${name}`, {
                method: 'GET',
                headers: {
                    "Content-Type": "application/json"
                }
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    /**
     * client 테이블 새로고침
     * */
    const refreshGridData = () => {
        getClient("").then(res => {
            clientGrid.resetData(res.data);
        })
    }

    /**
     * 사업자등록번호 검증 요청 api. 내부서버로 요청 보냄.
     * @param {string} businessNumber 사업자등록번호
     * @return JSON
     * */
    const validateBusinessNumber = async (businessNumber) => {
        try {
            // 사업자등록번호 숫자 10자리 validation
            if (!businessNumber.match(/^[0-9]{10}$/)) {
                alertModalText.textContent = "사업자등록번호는 10자리의 숫자만 허용됩니다.";
                alertModal.show();

                return new Error("사업자등록번호는 10자리의 숫자만 허용됩니다.");
            }

            const res = await fetch(`/api/client/openapi/businessnumber`, {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                // 공공 api requestBody 형식에 맞게 전송
                body: JSON.stringify({
                    b_no: [businessNumber]
                })
            })

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }


    /**
     * 개별 셀 비어있는지 검사
     * @param {any} row 셀
     * */
    const hasEmptyRequiredFields = row => {
        return (
            !row.name || row.name.trim() === "" ||
            !row.business_number || row.business_number.trim() === "" ||
            !row.address || row.address.trim() === "" ||
            !row.contact_manager || row.contact_manager.trim() === "" ||
            !row.contact || row.contact.trim() === "" ||
            !row.ceo || row.ceo.trim() === ""
        );
    }


    /**
     * 입력받은 셀들 비어있는지 검사 후 에러 반환
     * @param {any} rows 셀
     * */
    const validateRows = rows => {
        rows.forEach(row => {
            if (hasEmptyRequiredFields(row)) {
                alertModalText.textContent = "빈칸 없이 모두 입력해야 합니다.";
                alertModal.show();
                throw new Error("빈칸 없이 모두 입력해야 합니다.");
            }
        });
    }

    /**
     * client 테이블 변경값 수정 요청 api
     * @return JSON
     * */
    const updateModifiedRows = () => {
        const { createdRows, updatedRows } = clientGrid.getModifiedRows();

        // 액션 없이 저장만 눌렀을 때 에러
        if (createdRows.length === 0 && updatedRows.length === 0) {
            alertModalText.textContent = "수정된 데이터가 없습니다.";
            alertModal.show();

            throw new Error("수정된 데이터가 없습니다.");
        }

        // 비어있는 셀 검사
        validateRows(createdRows);
        validateRows(updatedRows);

        // 공백검사 함수
        const isEmpty = value =>
            value === null ||
            value === undefined ||
            (typeof value === 'string' && value.trim() === '');

        // id, 비고 제외 공백검사 실시
        const validateBlankField = row => {
            return Object.entries(row).some(([key, value]) => {
                key !== 'id' && key !== 'remark' && isEmpty(value)
            });
        };

        // 삽입 body
        const POST_BODY = createdRows.filter(row => !row.id)
            .map(row => ({
                name: row.name,
                business_number: row.business_number,
                address: row.address,
                contact: row.contact,
                ceo: row.ceo,
                contact_manager: row.contact_manager,
                remark: row.remark,
            }));

        // 수정 body
        const PUT_BODY = updatedRows.filter(row => row.id)
            .map(row => ({
                id: row.id,
                name: row.name,
                business_number: row.business_number,
                address: row.address,
                contact: row.contact,
                ceo: row.ceo,
                contact_manager: row.contact_manager,
                remark: row.remark,
            }));

        // 폼 내부에 빈칸 있을 시 에러
        if (POST_BODY.find(validateBlankField) || POST_BODY.find(validateBlankField)) {
            alert('입력하지 않은 필드값이 존재합니다.');
            return;
        }

        try {
            // Promise.all로 일괄처리 하기 위한 request 배열
            const requestList = [];

            // POST 요청 request 배열에 삽입
            if (POST_BODY.length > 0) {
                requestList.push(
                    fetch(`/api/client`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(POST_BODY)
                    })
                )
            }

            // PUT 요청 request 배열에 삽입
            if (PUT_BODY.length > 0) {
                requestList.push(
                    fetch(`/api/client`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(PUT_BODY)
                    })
                )
            }

            // 일괄 요청
            Promise.all(requestList)
                .then(res => {
                    clientGrid.clearModifiedData();
                    refreshGridData();
                })

        } catch (e) {
            console.error(e);
        }

    }

    refreshGridData();

    // ========================================================================
    // 이벤트리스너

    /**
     * 사업자 등록번호 클릭시 입력 모달 띄우기 이벤트
     * */
    clientGrid.on('click', e => {
        if(!['ATH003', 'ATH005'].includes(window.user.authCode)) {
            return;
        }

        const { columnName, rowKey } = e;

        // 클릭한 셀의 컬럼명이 "사업자등록번호"일 경우 수행
        if (columnName === "business_number") {
            selectedRowDataKey = rowKey;

            confirmModal.show();
            // 사업자등록번호 등록버튼 비활성화(초기상태)
            confirmBtn.disabled = true;
        }
    })

    /**
     * client 테이블에 새로운 row 추가 이벤트
     * */
    document.querySelector("button[name='appendClientBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            // 테이블의 가장 첫번째 열에 빈 row 삽입
            clientGrid.appendRow(NEW_ROW, {
                at: 0,
                focus: true
            });
        });

    /**
     * client 테이블 변경사항 저장 이벤트
     * */
    document.querySelector("button[name='saveClientBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            updateModifiedRows();
            alertModalText.textContent = "저장이 완료되었습니다";
            alertModal.show();
        })

    /**
     * 사업자등록번호 검색 버튼 이벤트
     * */
    document.querySelector("button[name='modalSrchBtn']")
        .addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();

            const businessNumber = businessNumberInput.value;

            validateBusinessNumber(businessNumber).then(res => {
                // 사업자등록번호 검증 완료시 등록버튼 활성화
                if (res.data) {
                    confirmBtn.className = "btn btn-outline-primary";
                    confirmBtn.disabled = false;
                }
            })
        })

    /**
     * client 테이블 거래처명 검색 이벤트
     * */
    document.querySelector("button[name='srhBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            const keyword = document.querySelector("input[name='srhClient']").value;

            getClient(keyword).then(res => {
                clientGrid.resetData(res.data);
            })
        })

    /**
     * 사업자등록번호 입력 확인 이벤트
     * */
    confirmBtn.addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        const businessNumber = businessNumberInput.value;

        confirmBtn.className = "btn btn-secondary";

        // 해당 셀 검증 후 등록한 사업자등록번호 입력
        if (clientGrid.getRow(selectedRowDataKey)) {
            clientGrid.setValue(selectedRowDataKey, "business_number", businessNumber);
        }

        businessNumberInput.value = "";
        confirmModal.hide();
    })

    /**
     * alert 모달 닫기 이벤트
     * */
    alertBtn.addEventListener('click', e => {
        alertModal.hide();
    })
}

window.onload = () => {
    init();
}