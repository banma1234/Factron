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
                editor: 'text'
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

        // 유효성 검사 함수
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
                    alert("힝 완료!");
                })

        } catch (e) {
            console.error(e);
        }

    }

    getClient("").then(res => {
        clientGrid.resetData(res.data);
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
        })
}

window.onload = () => {
    init();
}