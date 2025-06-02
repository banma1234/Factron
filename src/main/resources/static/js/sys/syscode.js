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
                name: 'id',
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
                name: 'id',
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

const getDetailCode = async (id) => {
    try {
        const res = await fetch(`/sys/detail?id=${id}`, {
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

const sysInit = () => {
    // const mainGrid = initMainGrid();

    initMainGrid();
    initDetailGrid();

    // mainGrid.on('click', e => {
    //     const rowKey = e.rowKey;
    //     const rowData = testGrid.getRow(rowKey);
    //
    //     getDetailCode(rowData.id).then(res => {
    //         const detailGrid = initDetailGrid();
    //
    //         detailGrid.resetData(res.data);
    //     })

        // if (rowData && rowData.id) {
        //     const popup = window.open('/test-form', '_blank', 'width=800,height=600');
        //
        //     // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        //     const messageHandler = (event) => {
        //         if (event.data === 'ready') {
        //             popup.postMessage({
        //                 name: rowData.name,
        //                 age: rowData.id,
        //                 birth: rowData.birth,
        //                 regDate: rowData.regDate,
        //                 remark: rowData.address
        //             }, "*");
        //             window.removeEventListener("message", messageHandler);
        //         }
        //     };
        //     window.addEventListener("message", messageHandler);
        // }
    // });

}

document.getElementById('postSysMainBtn').addEventListener('click', (e) => {
    e.preventDefault();
    e.stopPropagation();

    window.open('/sys-form', '_blank', 'width=800,height=400');
})

window.onload = () => {
    sysInit();
}