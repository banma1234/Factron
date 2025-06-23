// 대메뉴 열고 닫기
document.querySelectorAll('.menu-toggle').forEach(menu => {
    menu.addEventListener('click', function (e) {
        e.preventDefault();

        const parent = this.closest('.nav-item');
        const isOpen = parent.classList.contains('open');

        handleSidebarMenu();

        if (!isOpen) {
            parent.classList.add('open');
        }
    });
});

const handleSidebarMenu = () => {
    const sidebarMenu = document.querySelectorAll('.nav-item');
    sidebarMenu.forEach((menu) => {
        menu.classList.remove('open');
    });
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

document.getElementById('overlay').addEventListener('click', function (e) {
    toggleSidebar();
})
document.querySelector('.hamberger').addEventListener('click', function () {
    toggleSidebar();
})

// 사이드바 토글
const toggleSidebar = () => {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');

    sidebar.classList.toggle('active');
    overlay.classList.toggle('active');
}

// 페이지 이동 시 url에 맞는 메뉴 열기
document.addEventListener('DOMContentLoaded', function () {
    const menuItems = document.querySelectorAll('a.sub-link');
    const currentUrl = window.location.pathname;

    menuItems.forEach(function(menuItem) {
        const menuLink = menuItem.getAttribute('href');

        if (currentUrl === menuLink || currentUrl === menuLink + '/') {
            // 서브 링크에 active 클래스 추가
            menuItem.classList.add('active');

            // 상위 메뉴 열기
            const navItem = menuItem.closest('.nav-item');
            if (navItem) {
                navItem.classList.add('open');
            }
        }
    });
    displayUserInfo();
});