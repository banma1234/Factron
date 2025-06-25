const NEW_ROW = {
    id: undefined,
    name: '',
    business_number: '',
    address: '',
    contact: '',
    ceo: '',
    contact_manager: '',
    remark: ''
}

const init = () => {

    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const confirmBtn = document.querySelector("button[name='confirmBtn']");
    const alertBtn = document.querySelector("button[name='alertBtn']");
    const alertModalText = document.querySelector(".alert-modal-text");
    const businessNumberInput = document.querySelector("input[name='srchBusinessNumber']");
    let selectedRowDataKey = undefined;

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
                editor: 'text'
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
                editor: 'text'
            },
            {
                header: '연락처',
                name: 'contact',
                align: 'center',
                editor: 'text'
            },
            {
                header: '대표자',
                name: 'ceo',
                align: 'center',
                editor: 'text'
            },
            {
                header: '담당자',
                name: 'contact_manager',
                align: 'center',
                editor: 'text'
            },
            {
                header: '비고',
                name: 'remark',
                align: 'center',
                editor: 'text'
            },
        ]
    );

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

    const refreshGridData = () => {
        getClient("").then(res => {
            clientGrid.resetData(res.data);
        })
    }

    const validateBusinessNumber = async (businessNumber) => {
        try {
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
                body: JSON.stringify({
                    b_no: [businessNumber + ""]
                })
            })

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    const updateModifiedRows = () => {
        const { createdRows, updatedRows } = clientGrid.getModifiedRows();

        if (createdRows.length === 0 && updatedRows.length === 0) {
            alert('수정된 데이터가 없습니다.');
            return;
        }

        const isEmpty = value =>
            value === null ||
            value === undefined ||
            (typeof value === 'string' && value.trim() === '');

        const validateBlankField = row => {
            return Object.entries(row).some(([key, value]) => {
                key !== 'id' && key !== 'remark' && isEmpty(value)
            });
        };

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

        if (POST_BODY.find(validateBlankField) || POST_BODY.find(validateBlankField)) {
            alert('입력하지 않은 필드값이 존재합니다.');
            return;
        }

        try {
            const requestList = [];

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

    clientGrid.on('click', e => {
        const { columnName, rowKey } = e;

        if (columnName === "business_number") {
            selectedRowDataKey = rowKey;

            confirmModal.show();
            confirmBtn.disabled = true;
        }
    })

    document.querySelector("button[name='appendClientBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            clientGrid.appendRow(NEW_ROW, {
                at: 0,
                focus: true
            });
        });

    document.querySelector("button[name='saveClientBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            updateModifiedRows();
            alertModalText.textContent = "저장이 완료되었습니다";
            alertModal.show();
        })

    document.querySelector("button[name='modalSrchBtn']")
        .addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();

            const businessNumber = businessNumberInput.value;

            validateBusinessNumber(businessNumber).then(res => {
                if (res.data) {
                    confirmBtn.className = "btn btn-outline-primary";
                    confirmBtn.disabled = false;
                }
            })
        })

    document.querySelector("button[name='srhBtn']")
        .addEventListener('click', e => {
            e.preventDefault();

            const keyword = document.querySelector("input[name='srhClient']").value;

            getClient(keyword).then(res => {
                clientGrid.resetData(res.data);
            })
        })

    confirmBtn.addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        const businessNumber = businessNumberInput.value;

        confirmBtn.className = "btn btn-secondary";

        if (clientGrid.getRow(selectedRowDataKey)) {
            clientGrid.setValue(selectedRowDataKey, "business_number", businessNumber);
        }

        businessNumberInput.value = "";
        confirmModal.hide();
    })

    alertBtn.addEventListener('click', e => {
        alertModal.hide();
    })
}

window.onload = () => {
    init();
}