const DEFAULT_GRID_THEME = {
    cell: {
        normal: {
            border: 'gray'
        },
        header: {
            background: 'gray',
            text: 'white',
            border: 'gray'
        },
        rowHeaders: {
            header: {
                background: 'gray',
                text: 'white'
            }
        }
    }
};

const Grid = tui.Grid;
Grid.applyTheme('default', DEFAULT_GRID_THEME);

// grid 초기화
const initMainGrid = () => {
    // sys-main 세팅
    return new Grid({
        el: document.getElementById('grid_main'),
        scrollX: false,
        scrollY: true,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            {
                header: '구분코드',
                name: 'main_code',
                align: 'center'
            },
            {
                header: '구분명',
                name: 'name',
                align: 'center'
            },
            {
                header: '사용여부',
                name: 'is_active',
                align: 'center',
            },
        ],
    });
}

const initDetailGrid = () => {
    // sys-detail 세팅
    return new Grid({
        el: document.getElementById('grid_detail'),
        scrollX: false,
        scrollY: true,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            {
                header: '구분코드',
                name: 'detail_code',
                align: 'center'
            },
            {
                header: '구분명',
                name: 'name',
                align: 'center'
            },
            {
                header: '사용여부',
                name: 'is_active',
                align: 'center',
            },
        ],
    })
}

/*
* 메인코드 불러오는 api
* */
const getMainCode = async () => {
    try {
        const res = await fetch(`/api/sys/main`, {
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

/*
* 상세코드 불러오는 api
* */
const getDetailCode = async (mainCode) => {
    try {
        const res = await fetch(`/api/sys/detail?id=${mainCode}`, {
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

let selectedRowData = null;

const sysInit = () => {
    const mainGrid = initMainGrid();
    const detailGrid = initDetailGrid();

    const setDetailGridEvent = () => {
        detailGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = detailGrid.getRow(rowKey);

            console.log("detail : ", selectedRowData);
        })
    }

    const setMainGridEvent = () => {
        mainGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = mainGrid.getRow(rowKey);

            console.log("main : ", selectedRowData);

            getDetailCode(selectedRowData.id).then(res => {
                detailGrid.resetData(res.data);

                setDetailGridEvent();
            })
        });
    }

    getMainCode().then(res => {
        mainGrid.resetData(res.data);

        setMainGridEvent();
    })

};

const openUpdatePopup = () => {
    const rowData = selectedRowData;

    if (rowData && rowData.id) {
        const popup = window.open('/sys/sys-form', '_blank', 'width=800,height=400');

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({
                    main_code: rowData.main_code ? rowData.main_code : "",
                    detail_code: rowData.detail_code ? rowData.detail_code : "",
                    name: rowData.name,
                    is_active: rowData.is_active
                }, "*");

                window.removeEventListener("message", messageHandler);
            }
        };

        window.addEventListener("message", messageHandler);
    }
}

document.querySelectorAll(".updateSysCodeBtn")
    .forEach(btn => {
        btn.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();

                openUpdatePopup();
            });
    });


document.getElementById('postSysMainBtn')
    .addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        window.open('/sys/sys-form', '_blank', 'width=800,height=400');
    });

window.onload = () => {
    sysInit();
};