/**
 * 클릭한 `컬럼`의 데이터 저장하는 `전역변수`
 * */
let selectedRowData = null;

/**
 * window 로드 후 실행할 스크립트
 * @return void
 * */
const init = () => {
    /**
     * `LOT` 테이블 초기화
     * */
    const lotGrid = initGrid(
        document.getElementById('grid_lot'),
        400,
        [
            {
                header: '생성일자',
                name: 'created_at',
                align: 'center',
            },
            {
                header: 'id',
                name: 'id',
                align: 'center'
            },
            {
                header: '품목명',
                name: 'item_id',
                align: 'center'
            },
        ],
        ['rowNum']
    )

    /**
     * 입력받은 자식 노드 nodeStructure 포맷에 맞게 변환
     * @param {Object} node 트리구조 데이터
     * @return {Object} nodeStructure
     * */
    const convertTreetoTreantNode = node => {
        return {
            text: {
                name: `id : ${node.id}`,
                title: `유형 : ${node.eventType}`,
                desc: `품목 : ${node.materialId ? node.materialId : node.itemId}`,
                contact: `수량: ${node.quantity}`,
            },
            children: node.children?.map(convertTreetoTreantNode) || []
        }
    }

    /**
     * 입력받은 트리구조 데이터 Treant.js 포맷에 맞게 변환
     * @param {Object} data 트리구조 데이터
     * */
    const buildTree = data => {
        const treeWrapper = document.getElementById("tree-wrapper");
        treeWrapper.innerHTML = '<div id="lot-tree"></div>';

        const NEW_CONFIG = {
            chart: {
                container: "#lot-tree",
                connectors: { type: "step" },
                node: { HTMLclass: "lot-node" },
                animateOnInit: false,
                nodeAlign: 'top',
            },
            nodeStructure: convertTreetoTreantNode(data)
        }

        // 새로운 트리 생성
        new Treant(NEW_CONFIG);
    }

    /**
     * 최상위 node 요청 api
     * @return JSON
     * */
    const getRootData = async () => {
        try {
            const res = await fetch(`/api/lot/search`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    /**
     * 대상 LOT 트리구조 데이터 요청 api
     * @param {string} lotId LOT id
     * @return JSON
     * */
    const getTreeData = async (lotId) => {
        try {
            const res = await fetch(`/api/lot?lotId=${lotId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            return res.json();
        } catch (e) {
            console.error(e);
        }
    }

    /**
     * LOT 그리드 클릭 이벤트 매핑
     * */
    const setLotGridEvent = () => {
        lotGrid.on('click', e => {
            const rowKey = e.rowKey;
            selectedRowData = lotGrid.getRow(rowKey);

            getTreeData(selectedRowData.id).then(res => {
                buildTree(res.data);
            })
        })
    }

    getRootData().then(res => {
        lotGrid.resetData(res.data);

        setLotGridEvent();
    });
}

window.onload = () => {
    init();
}