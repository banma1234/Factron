let bomTree = null;

// tree 세팅
const initTree = (data) => {
    const container = document.getElementById("bomTree");

    // 이전 트리 제거 (있다면)
    if (bomTree) {
        container.innerHTML = '';
    }

    // 새 트리 생성
    bomTree = new tui.Tree(container, {
        data: data,
        nodeDefaultState: 'opened',
        usageStatistics: false, // 통계 비활성화
    }).enableFeature('Selectable', {
        selectedClassName: 'tui-tree-selected',  // 선택 시 클래스
    });

    // tree 행 클릭 시 상세정보 세팅
    bomTree.on('select', (e) => {
        const node = bomTree.getNodeData(e.nodeId);
        const raw = node._rawData;

        if (raw) {
            // 폼 세팅
            document.querySelector("input[name='childProdId']").value = raw.id;
            document.querySelector("input[name='childProdName']").value = raw.name;
            document.querySelector("input[name='prodType']").value = raw.type;
            document.querySelector("input[name='consumption']").value = raw.consumption;
        }
    });
};

// tree 구조로 변환
function buildTree(data) {
    const itemMap = new Map();
    const childrenMap = new Map();

    // 모든 항목을 맵에 저장
    data.forEach(d => {
        // 자식 노드 생성
        const childNode = {
            id: d.childProdId,
            text: `${d.childProdName} (${d.prodType})`,
            _rawData: {
                id: d.childProdId,
                name: d.childProdName,
                type: d.prodType,
                consumption: d.consumption
            },
            children: [] // 재귀 연결용
        };

        // parent node가 없으면 생성
        if (!itemMap.has(d.parentItemId)) {
            itemMap.set(d.parentItemId, {
                id: d.parentItemId,
                text: d.parentItemName,
                _rawData: {
                    id: d.childProdId,
                    name: d.childProdName,
                    type: d.prodType,
                    consumption: d.consumption
                },
                children: []
            });
        }

        // 자식 노드도 itemMap에 넣기 (나중에 부모가 될 수도 있으므로)
        if (!itemMap.has(d.childProdId)) {
            itemMap.set(d.childProdId, childNode);
        }

        // childrenMap에는 관계만 저장
        if (!childrenMap.has(d.parentItemId)) {
            childrenMap.set(d.parentItemId, []);
        }
        childrenMap.get(d.parentItemId).push(childNode);
    });

    // 재귀적으로 자식 붙이기
    function attachChildren(node) {
        const children = childrenMap.get(node.id) || [];
        node.children = children;
        for (const child of children) {
            attachChildren(child);
        }
    }

    // 루트 노드 찾기: 자식으로 등장하지 않은 부모
    const childIds = new Set(data.map(d => d.childProdId));
    const rootNodes = [];

    itemMap.forEach((node, id) => {
        if (!childIds.has(id)) {
            attachChildren(node); // 자식 연결
            rootNodes.push(node);
        }
    });

    return rootNodes;
}

const init = () => {
    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            const treeData = buildTree(res.data);
            initTree(treeData); // tree에 세팅
        });
    }, false);

    // 엔터 시 검색
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        // 조회
        getData().then(res => {
            const treeData = buildTree(res.data);
            initTree(treeData); // tree에 세팅
        });
    });

    // 목록 조회
    async function getData() {

        // fetch data
        const data = new URLSearchParams({
            srhIdOrName: document.querySelector("input[name='srhIdOrName']").value,
        });

        try {
            const res = await fetch(`/api/bom?${data.toString()}`, {
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

    // 페이지 진입 시 바로 리스트 호출
    getData().then(res => {
        const treeData = buildTree(res.data);
        initTree(treeData); // tree에 세팅
    });
}

window.onload = () => {
    init();
}