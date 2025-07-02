let selectedRowData = null;

const init = () => {
    const INITIAL_CONFIG = {
        chart: {
            container: "#lot-tree",
            connectors: { type: "step" },
            node: { HTMLclass: "lot-node" },
            animateOnInit: false
        },
        nodeStructure: {
            text: { name: "Parent node" }
        }
    };

    new Treant(INITIAL_CONFIG);

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

    const convertTreetoTreantNode = node => {
        return {
            text: {
                name: `id : ${node.id}`,
                title: `유형 : ${node.eventType}`,
                desc: `수량: ${node.quantity}`,
                data: `생성일: ${node.createdAt}`,
                contact: `생성자: ${node.createdBy}`,
            },
            children: node.children?.map(convertTreetoTreantNode) || []
        }
    }

    const buildTree = data => {
        const treeWrapper = document.getElementById("tree-wrapper");
        treeWrapper.innerHTML = '<div id="lot-tree" style="width:800px; height: 600px"></div>';

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

        new Treant(NEW_CONFIG);
    }

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