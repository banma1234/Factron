

const commonInit = () => {
    // 대메뉴 열고 닫기
    document.querySelectorAll('.menu-toggle').forEach(menu => {
        menu.addEventListener('click', function (e) {
            e.preventDefault();

            handleSidebarMenu();

            const parent = this.closest('.nav-item');
            parent.classList.add('open');
        });
    });

    const handleSidebarMenu = () => {
        const sidebarMenu = document.querySelectorAll('.nav-item');

        sidebarMenu.forEach((menu) => {
            menu.classList.remove('open');
        })
    }


// 메뉴 클릭 시 활성화 스타일 적용
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function () {
            // 기존 active 제거
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            // 현재 클릭된 링크에 active 추가
            this.classList.add('active');
        });
    });

// 사이드바 토글
    const toggleSidebar = () => {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');

        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
    }
}

commonInit();