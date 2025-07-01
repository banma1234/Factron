let materialGrid; // 그리드 변수
let selectedItems = []; // 선택한 자재 목록

const init = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.querySelector(".confirmModal"));
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));

    // 1) 자재 그리드 initGrid 함수로 생성
    materialGrid = initGrid(
        document.getElementById("materialGrid"),
        300,
        [
            { header: "자재 ID", name: "materialId", align: "center"  },
            { header: "자재명", name: "name", align: "center" },
            { header: "단위", name: "unit", align: "center" },
            { header: "규격", name: "spec", align: "center" },
        ]
    );

    // 2) 자재 검색 함수
    async function loadMaterials(keyword = "") {
        try {
            const res = await fetch(`/api/material?materialName=${encodeURIComponent(keyword)}`);
            const json = await res.json();
            materialGrid.resetData(json.data || []);
        } catch (e) {
            console.error("자재 조회 실패", e);
            alert("자재 목록 조회에 실패했습니다.");
        }
    }

    // 3) 초기 데이터 로드
    loadMaterials();

    // 4) 검색 이벤트
    form.querySelector("input[name='materialSearch']").addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
            loadMaterials(e.target.value.trim());
        }
    });

    form.querySelector(".materialSearchBtn").addEventListener("click", () => {
        const keyword = form.querySelector("input[name='materialSearch']").value.trim();
        loadMaterials(keyword);
    });

    // 5) 그리드 더블 클릭 이벤트 - 발주 자재 목록에 추가
    materialGrid.on("dblclick", ev => {
        if (ev.rowKey != null) {
            const row = materialGrid.getRow(ev.rowKey);
            addItemToPurchaseList(row);
        }
    });

    // 6) 발주 자재 목록 추가 함수
    function addItemToPurchaseList(item) {
        if (selectedItems.find(i => i.materialId === item.materialId)) {
            alert("이미 추가된 자재입니다.");
            return;
        }
        if (!item.quantity) item.quantity = 1;
        if (!item.price) item.price = 1000;

        selectedItems.push(item);
        renderSelectedItems();
    }

    // 7) 발주 자재 목록 렌더링
    function renderSelectedItems() {
        const container = document.querySelector(".purchase-items");
        container.innerHTML = "";
        let total = 0;
        selectedItems.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            div.innerHTML = `
                <span>${item.name} × ${item.quantity}개</span>
                <span>₩${(item.price * item.quantity).toLocaleString()}</span>
            `;
            container.appendChild(div);
            total += item.price * item.quantity;
        });
        document.querySelector("span[name='totalAmount']").textContent = `₩${total.toLocaleString()}`;
    }

    // 8) 등록 버튼 클릭
    document.querySelector(".saveBtn").addEventListener("click", () => {
        const clientId = form.querySelector("select[name='clientId']").value;
        if (!clientId) {
            alert("거래처를 선택해주세요.");
            return;
        }
        if (!selectedItems.length) {
            alert("발주 자재를 선택해주세요.");
            return;
        }
        confirmModal.show();
    });

    // 9) 확인 모달에서 등록 처리
    document.querySelector(".confirmRegisterBtn").addEventListener("click", async () => {
        confirmModal.hide();

        const clientId = form.querySelector("select[name='clientId']").value;

        const payload = {
            clientId,
            items: selectedItems.map(item => ({
                materialId: item.materialId,
                quantity: item.quantity
            }))
        };

        try {
            const res = await fetch("/api/purchase", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const result = await res.json();

            document.querySelector(".alertModal .modal-body").textContent =
                result?.message || "등록이 완료되었습니다.";
            alertModal.show();

        } catch (e) {
            console.error("등록 오류:", e);
            alert("등록에 실패했습니다.");
        }
    });

    // 10) 알림 모달 닫기
    document.querySelector(".alertCloseBtn").addEventListener("click", () => {
        alertModal.hide();
        if (window.opener) window.opener.getData?.();
        window.close();
    });

    // 11) 취소 버튼 클릭 시 창 닫기
    document.querySelector(".cancelBtn").addEventListener("click", () => {
        window.close();
    });
};

window.onload = () => {
    init();
};
