// const setStorage = async () => {
//     const storages = await fetch(`/api/storage`, {
//         method: 'GET',
//         headers: {
//             'Content-Type': 'application/json'
//         }})
//         .then(res => res.json())
//         .then(res => {
//             return res.data;
//         })
//         .catch(e => {
//             alert(e);
//         });
//
//     const selectTag = document.querySelector("select[name='storage']");
//
//     storages.forEach((storage) => {
//         const optionElement = document.createElement("option");
//         optionElement.value = storage.id;
//         optionElement.textContent = storage.name;
//
//         if(selectTag){
//             selectTag.appendChild(optionElement);
//         }
//     });
// }



const init = () => {
    const getStockData = async () => {
        const data = new URLSearchParams();
        const srhIdOrName = document.querySelector("input[name='srhInput']").value;
        // const storageId = document.querySelector("select[name='storage']").value;
        const productTypeCode = document.querySelector("select[name='productType']").value;

        if(srhIdOrName) data.append("srhIdOrName", srhIdOrName);
        // if(storageId) data.append("storageId", storageId);
        if(productTypeCode) data.append("productTypeCode", productTypeCode);

        // stockGrid.resetData(STOCK_DATA);
        await fetch(`/api/stock?${data.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    return stockGrid.resetData(res.data); // grid에 세팅
                }else{
                    alert(res.message);
                }
                return stockGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    const stockGrid = initGrid(
        document.getElementById('stock_grid'),
        400,
        [
            {
                header: '재고번호',
                name: 'id',
                align: 'center'
            },
            {
                header: '품목 이름',
                name: 'productName',
                align: 'center'
            },
            {
                header: '품목 종류',
                name: 'productTypeName',
                align: 'center'
            },
            {
                header: '수량',
                name: 'quantity',
                align: 'center'
            },
            {
                header: '창고명',
                name: 'storageName',
                align: 'center'
            }
        ]
    );

    setSelectBox("ITP", 'productType');
    // setStorage();
    getStockData();

    const STOCK_DATA =  [
        {
            stockId: 1,
            productId: 'ITEM0001',
            productName: 'ITEM_NAME',
            productTypeCode: 'ITEM_TYPE_CODE',
            productTypeName: 'ITEM_TYPE_NAME',
            quantity: 100,
            storageId: 1,
            storageName: 'STORAGE_NAME'
        }
    ]

    const srhBtn = document.querySelector("button[name='productSrhBtn']");

    if (srhBtn) {
        srhBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            getStockData();
        })
    }
}

window.onload = () => {
    init();
}