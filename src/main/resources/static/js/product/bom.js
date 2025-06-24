let bomTree = null;
let editMode = false;
const form = document.querySelector("div.saveForm");

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
            toggleEditButtons(null);

            // 데이터 세팅
            form.querySelector("input[name='id']").value = raw.bomId || '';
            form.querySelector("input[name='childProdId']").value = raw.id || '';
            form.querySelector("input[name='childProdName']").value = raw.name || '';
            form.querySelector("input[name='prodType']").value = raw.type || '';
            form.querySelector("input[name='consumption']").value = formatNumber(raw.consumption) || '-';
            form.querySelector("span.unitName").textContent = raw.unit || '';
            form.querySelector("input[name='parentProdId']").value = raw.parentId || '';
            form.querySelector("input[name='parentProdName']").value = raw.parentName || '';
        }
    });
};

// tree 구조로 변환
function buildTree(data) {
    const itemMap = new Map();
    const childrenMap = new Map();

    // 모든 항목을 맵에 저장
    data.forEach(d => {
        // parent node가 없으면 생성
        if (!itemMap.has(d.parentItemId)) {
            itemMap.set(d.parentItemId, {
                id: d.parentItemId,
                text: d.parentItemName,
                _rawData: {
                    bomId: d.id,
                    id: d.parentItemId,
                    name: d.parentItemName,
                    type: '완제품',
                    consumption: '',
                    unit: '',
                    parentId: '',
                    parentName: ''
                },
                children: []
            });
        }

        // childProdId가 null이 아니면 자식 노드 생성
        if (d.childProdId != null) {
            const childNode = {
                id: d.childProdId,
                text: `${d.childProdName} (${d.prodType})`,
                _rawData: {
                    bomId: d.id,
                    id: d.childProdId,
                    name: d.childProdName,
                    type: d.prodType,
                    consumption: d.consumption,
                    unit: d.unitName,
                    parentId: d.parentItemId,
                    parentName: d.parentItemName
                },
                children: [] // 재귀 연결용
            };

            // 자식 노드도 itemMap에 넣기 (나중에 부모가 될 수도 있으므로)
            if (!itemMap.has(d.childProdId)) {
                itemMap.set(d.childProdId, childNode);
            }

            // childrenMap에는 관계만 저장
            if (!childrenMap.has(d.parentItemId)) {
                childrenMap.set(d.parentItemId, []);
            }
            childrenMap.get(d.parentItemId).push(childNode);
        }
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
    const childIds = new Set(data.filter(d => d.childProdId != null).map(d => d.childProdId));
    const rootNodes = [];

    itemMap.forEach((node, id) => {
        if (!childIds.has(id)) {
            attachChildren(node); // 자식 연결
            rootNodes.push(node);
        }
    });

    return rootNodes;
}

// 버튼 모드 변경
function toggleEditButtons(mode) {
    const editGroup = document.querySelector(".editBOM");
    const confirmGroup = document.querySelector(".confirmBOM");

    if (mode) {
        // 수정 모드
        if (mode === 'add') {
            form.querySelectorAll("input[name='childProdId'], input[name='childProdName'], input[name='prodType'], input[name='consumption']").forEach(input => {
                input.disabled = false;
            });

            editGroup.classList.add("d-none");
            confirmGroup.classList.remove("d-none");
        }

        if (mode === 'edit') {
            const consumption = form.querySelector("input[name='consumption']");
            consumption.disabled = false;
            consumption.value = unformatNumber(consumption.value);

            editGroup.classList.add("d-none");
            confirmGroup.classList.remove("d-none");
        }

        editMode = mode; // 'add', 'edit', 'delete' 중 하나

    } else {
        // 읽기 모드
        form.querySelectorAll("input[name='childProdId'], input[name='childProdName'], input[name='prodType'], input[name='consumption']").forEach(input => {
            input.disabled = true;
        });

        form.querySelector("input[name='id']").value = '';
        form.querySelector("input[name='parentProdId']").value = '';
        form.querySelector("input[name='parentProdName']").value = '';
        form.querySelector("input[name='childProdId']").value = '';
        form.querySelector("input[name='childProdName']").value = '';
        form.querySelector("input[name='prodType']").value = '';
        form.querySelector("input[name='consumption']").value = '';
        form.querySelector("span.unitName").textContent = '';

        editGroup.classList.remove("d-none");
        confirmGroup.classList.add("d-none");
        editMode = false;
    }
}

// 소요량 validation
const isValidPositiveNumber = (val) => {
    const num = unformatNumber(val);
    return /^(?:\d+|\d+\.\d+)$/.test(num); // 정수 또는 소수, 0 이상
};

const init = () => {
    const saveBtn = document.querySelector("button.saveBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            toggleEditButtons(null);

            const treeData = buildTree(res.data);
            initTree(treeData); // tree에 세팅
        });
    }, false);

    // 엔터 시 검색
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        // 조회
        getData().then(res => {
            toggleEditButtons(null);

            const treeData = buildTree(res.data);
            initTree(treeData); // tree에 세팅
        });
    });

    // 추가 버튼
    document.querySelector(".registBOM").addEventListener("click", function(e) {
        e.preventDefault();

        const selectedId = bomTree.getSelectedNodeId();
        if (!selectedId) {
            alert("상위 품목을 먼저 선택하세요.");
            return;
        }

        const node = bomTree.getNodeData(selectedId);
        const raw = node._rawData;
        if (raw) {
            if (raw.type === '원자재') {
                alert("원자재는 하위 BOM을 등록할 수 없습니다.");
                return;
            }

            // 부모 정보는 선택된 노드 기준
            document.querySelector("input[name='parentProdId']").value = raw.id || '';
            document.querySelector("input[name='parentProdName']").value = raw.name || '';

            // 입력창 초기화
            document.querySelector("input[name='id']").value = '';
            document.querySelector("input[name='childProdId']").value = '';
            document.querySelector("input[name='childProdName']").value = '';
            document.querySelector("input[name='prodType']").value = '';
            document.querySelector("input[name='consumption']").value = '';
            document.querySelector("span.unitName").textContent = '';
        }

        // 하드코딩
        form.querySelector("input[name='childProdId']").value = 'M0000007';
        form.querySelector("input[name='childProdName']").value = '스티커 라벨(하드코딩)';
        form.querySelector("input[name='prodType']").value = '원자재';
        form.querySelector("span.unitName").textContent = '개';

        toggleEditButtons("add");
    }, false);

    // 수정 버튼
    document.querySelector(".updateBOM").addEventListener("click", function(e) {
        e.preventDefault();

        const selectedId = bomTree.getSelectedNodeId();
        if (!selectedId) {
            alert("수정할 BOM을 먼저 선택하세요.");
            return;
        }

        const node = bomTree.getNodeData(selectedId);
        const raw = node._rawData;
        if (raw) {
            if (raw.type === '완제품') {
                alert("완제품 BOM은 수정할 수 없습니다.");
                return;
            }

            toggleEditButtons("edit");
        }
    });

    // 삭제 버튼
    document.querySelector(".deleteBOM").addEventListener("click", function(e) {
        e.preventDefault();

        const selectedId = bomTree.getSelectedNodeId();
        if (!selectedId) {
            alert("삭제할 BOM을 먼저 선택하세요.");
            return;
        }

        const node = bomTree.getNodeData(selectedId);
        const raw = node._rawData;
        if (raw) {
            if (raw.type === '완제품') {
                alert("완제품 BOM은 삭제할 수 없습니다.");
                return;
            }

            toggleEditButtons("delete");

            document.querySelector(".confirmModal .modal-body").innerHTML = `선택한 BOM 및 연결된 모든 하위 BOM이 삭제됩니다.<br/>삭제하시겠습니까?`;
            confirmModal.show();
        }
    });

    // 저장 버튼
    saveBtn.addEventListener("click", function(e) {
        e.preventDefault();

        if (editMode === 'add') {
            // BOM 등록
            if (form.querySelector("input[name='childProdId']").value === '') {
                alert('품목을 선택해주세요.');
                return;
            }

            const consumption = form.querySelector("input[name='consumption']").value.trim();
            if (consumption === '') {
                alert('소요량을 입력해주세요.');
                return;
            }
            if (!isValidPositiveNumber(consumption)) {
                alert('소요량은 0 이상의 숫자만 입력 가능합니다.');
                return;
            }
        }
        if (editMode === 'edit') {
            // BOM 수정
            const consumption = form.querySelector("input[name='consumption']").value.trim();
            if (consumption === '') {
                alert('소요량을 입력해주세요.');
                return;
            }
            if (!isValidPositiveNumber(consumption)) {
                alert('소요량은 0 이상의 숫자만 입력 가능합니다.');
                return;
            }
        }

        document.querySelector(".confirmModal .modal-body").innerHTML = `BOM 정보를 저장하시겠습니까?`;
        confirmModal.show();
    });

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        saveData().then(res => {
            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        // 데이터 재조회
        getData().then(res => {
            toggleEditButtons(null);

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
            alert("조회 중 오류가 발생했습니다.");
            console.error(e);
        }
    }

    // 저장
    async function saveData() {
        let method = 'POST';

        // fetch data
        const data = {
            id: form.querySelector("input[name='id']").value,
            consumption: unformatNumber(form.querySelector("input[name='consumption']").value),
        };

        if (editMode === 'add') {
            // 등록일 경우 부모,자식 품목 정보 추가
            const prodType = form.querySelector("input[name='prodType']").value;
            const childProdId = form.querySelector("input[name='childProdId']").value;
            if (prodType === '원자재') {
                data.childMaterialId = childProdId;
            } else {
                data.childItemId = childProdId;
            }
            data.parentItemId = form.querySelector("input[name='parentProdId']").value;
        }

        if (editMode === 'edit') {
            method = 'PUT';
        }

        if (editMode === 'delete') {
            method = 'DELETE';
        }

        try {
            const res = await fetch(`/api/bom`, {
                method: method,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            alert("저장 중 오류가 발생했습니다.");
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