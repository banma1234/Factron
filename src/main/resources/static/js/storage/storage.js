const init = () => {

    const storageGrid = initGrid(
        document.getElementById('grid_storage'),
        400,
        [
            {
                header: 'id',
                name: 'id',
                hidden: true
            },
            {
                header: '창고명',
                name: 'name',
                align: 'center'
            },
            {
                header: '주소',
                name: 'address',
                align: 'center'
            },
            {
                header: '면적',
                name: 'area',
                align: 'center',
            },
            {
                header: '창고유형',
                name: 'type_code',
                align: 'center',
            }
        ]
    )

    const getStorageInfo = async (name) => {
        try {
            const res = await fetch(`/api/storage?name=${name}`, {
                method: 'GET',
                headers: {
                    "Content-Type": "application/json"
                }
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    };

    getStorageInfo("").then(res => {
        storageGrid.resetData(res.data);
    })
}

window.onload = () => {
    init();
}