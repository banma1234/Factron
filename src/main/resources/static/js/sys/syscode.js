// grid 초기화
const initGrid = () => {
    const Grid = tui.Grid;

    // 테마
    Grid.applyTheme('default',  {
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
    });

    // 세팅
    return new Grid({
        el: document.getElementById('grid'),
        scrollX: false,
        scrollY: false,
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

const sysInit = () => {
    // const syscodeGrid = initGrid();
    initGrid();
    console.log("wow");
}

window.onload = () => {
    sysInit();
}