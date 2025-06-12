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
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
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

const mainGrid = initMainGrid();
const detailGrid = initDetailGrid();
let selectedRowData = null;

const sysInit = () => {

    const setDetailGridEvent = () => {
        detailGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = detailGrid.getRow(rowKey);
        })
    }

    const setMainGridEvent = () => {
        mainGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = mainGrid.getRow(rowKey);

            getDetailCode(selectedRowData.main_code).then(res => {
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

const refreshDataOnPopup = async () => {
    await getMainCode().then(res => {
        mainGrid.resetData(res.data);
    })

    if (selectedRowData) {
        console.log(selectedRowData);
        await getDetailCode(selectedRowData.main_code).then(res => {
            detailGrid.resetData(res.data);
        })
    }
};

const openUpdatePopup = (isEditMode) => {
    const rowData = selectedRowData || undefined;
    let url = "/sys/sys-form?target=";

    if (!rowData) {
        url += "main"
    } else if (!rowData.detail_code && isEditMode) {
        url += "main"
    } else {
        url += "detail";
    }

    const popup = window.open(
        url,
        '_blank',
        'width=800,height=400'
    );

    // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
    const messageHandler = (event) => {
        if (event.data === 'ready') {

            popup.postMessage({
                main_code: rowData.main_code ? rowData.main_code : "",
                detail_code: rowData.detail_code && isEditMode ? rowData.detail_code : "",
                name: rowData.name,
                is_active: rowData.is_active,
                is_edit_mode: isEditMode,
            }, "*");

            window.removeEventListener("message", messageHandler);
        }
    };

    window.addEventListener("message", messageHandler);
}

document.querySelectorAll(".updateSysCodeBtn")
    .forEach(btn => {
        btn.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();

                openUpdatePopup(true);
            });
    });

document.querySelector("button[name='postSysMainBtn']")
    .addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        openUpdatePopup(false);
    });

document.querySelector("button[name='postSysDetailBtn']")
    .addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();

        openUpdatePopup(false);
    });

window.sysInit = sysInit;
window.refreshDataOnPopup = refreshDataOnPopup;

window.onload = () => {
    sysInit();
};