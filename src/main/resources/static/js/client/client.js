const init = () => {

    const clientGrid = initGrid(
        document.getElementById('grid_client'),
        400,
        [
            {
                header: '거래처명',
                name: 'name',
                align: 'center'
            },
            {
                header: '사업자등록번호',
                name: 'business_number',
                align: 'center'
            },
            {
                header: '주소',
                name: 'address',
                align: 'center',
            },
            {
                header: '연락처',
                name: 'contact',
                align: 'center',
            },
            {
                header: '대표자',
                name: 'ceo',
                align: 'center',
            },
            {
                header: '담당자',
                name: 'contact_manager',
                align: 'center',
            },
            {
                header: '비고',
                name: 'remark',
                align: 'center',
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

    getClient("").then(res => {
        clientGrid.resetData(res.data());
    })
}

window.onload = () => {
    init();
}