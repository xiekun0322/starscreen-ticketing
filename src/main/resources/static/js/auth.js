/**
 * 【功能】全局登录态 + 弹窗 + apiFetch 包装器。
 * 【改动】"发送验证码"改成：后端返回验证码 → alert 显示 → 自动填入
 */
(function () {
    'use strict';

    window.currentUser = null;

    window.refreshCurrentUser = function () {
        return fetch('/api/user/current', { credentials: 'same-origin' })
            .then(r => r.json())
            .then(res => {
                window.currentUser = (res.code === 200) ? res.data : null;
                updateNavUI();
                return window.currentUser;
            })
            .catch(() => {
                window.currentUser = null;
                updateNavUI();
                return null;
            });
    };

    function updateNavUI() {
        const navRight = document.querySelector('.nav-right .user-area');
        if (!navRight) return;
        if (window.currentUser) {
            navRight.innerHTML =
                '<a href="/orders" style="color:#1e3a8a;margin-right:16px;font-size:14px;">🎟️ 我的订单</a>' +
                '<div class="user-dropdown">' +
                    '<span class="user-name">👤 ' + escapeHtml(window.currentUser.username) + ' ▼</span>' +
                    '<div class="dropdown-menu">' +
                        '<a href="/user/profile">个人中心</a>' +
                        '<a href="/orders">我的订单</a>' +
                        '<a href="/admin">后台管理</a>' +
                        '<a href="javascript:void(0)" onclick="openBindModal()">绑定账号</a>' +
                        '<a href="javascript:void(0)" onclick="doLogout()">退出登录</a>' +
                    '</div>' +
                '</div>';
        } else {
            navRight.innerHTML =
                '<a href="javascript:void(0)" onclick="openLoginModal()" ' +
                'style="color:#1e3a8a;font-weight:bold;font-size:14px;">登录 / 注册</a>';
        }
    }

    function escapeHtml(s) {
        return String(s).replace(/[&<>"']/g, c => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        }[c]));
    }

    // ==================== 登录模态框 ====================
    let afterLoginCallback = null;

    window.openLoginModal = function (callback) {
        afterLoginCallback = callback || null;
        const modal = document.getElementById('globalLoginModal');
        if (!modal) {
            sessionStorage.setItem('loginBack', window.location.pathname);
            window.location.href = '/login';
            return;
        }
        modal.classList.add('show');
        setTimeout(() => {
            const input = document.getElementById('glmPwdUsername');
            if (input) input.focus();
        }, 100);
    };

    window.closeLoginModal = function () {
        const modal = document.getElementById('globalLoginModal');
        if (modal) modal.classList.remove('show');
        afterLoginCallback = null;
    };

    function onLoginSuccess(user) {
        window.currentUser = user;
        updateNavUI();
        const modal = document.getElementById('globalLoginModal');
        if (modal) modal.classList.remove('show');
        if (typeof afterLoginCallback === 'function') {
            const cb = afterLoginCallback;
            afterLoginCallback = null;
            setTimeout(cb, 50);
        }
    }

    // ==================== apiFetch ====================
    window.apiFetch = function (url, options) {
        options = options || {};
        options.credentials = 'same-origin';
        return fetch(url, options).then(res => {
            if (res.status === 401) {
                return new Promise((resolve, reject) => {
                    window.openLoginModal(function () {
                        fetch(url, options).then(resolve).catch(reject);
                    });
                    reject({ __needLogin: true, message: '未登录' });
                });
            }
            return res;
        });
    };

    window.doLogout = function () {
        if (!confirm('确定要退出登录吗？')) return;
        fetch('/api/user/logout', { method: 'POST', credentials: 'same-origin' })
            .then(() => { window.location.href = '/'; })
            .catch(() => { window.location.href = '/'; });
    };

    // ==================== 登录模态框事件 ====================
    function bindLoginModalEvents() {
        const modal = document.getElementById('globalLoginModal');
        if (!modal) return;

        const tabSms = document.getElementById('glmTabSms');
        const tabPwd = document.getElementById('glmTabPwd');
        const panelSms = document.getElementById('glmPanelSms');
        const panelPwd = document.getElementById('glmPanelPwd');
        const msg = document.getElementById('glmMsg');

        function showMsg(text, type) {
            msg.textContent = text;
            msg.className = 'glm-msg ' + (type || '');
        }

        tabSms.addEventListener('click', () => {
            tabSms.classList.add('active'); tabPwd.classList.remove('active');
            panelSms.classList.add('active'); panelPwd.classList.remove('active');
            msg.className = 'glm-msg';
        });
        tabPwd.addEventListener('click', () => {
            tabPwd.classList.add('active'); tabSms.classList.remove('active');
            panelPwd.classList.add('active'); panelSms.classList.remove('active');
            msg.className = 'glm-msg';
        });

        // ===== 账号密码登录 =====
        const pwdUser = document.getElementById('glmPwdUsername');
        const pwdPass = document.getElementById('glmPwdPassword');
        const pwdBtn = document.getElementById('glmPwdLoginBtn');

        function checkPwd() { pwdBtn.disabled = !(pwdUser.value.trim() && pwdPass.value.trim()); }
        pwdUser.addEventListener('input', checkPwd);
        pwdPass.addEventListener('input', checkPwd);

        pwdBtn.addEventListener('click', () => {
            pwdBtn.disabled = true; pwdBtn.textContent = '登录中...';
            fetch('/api/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({
                    username: pwdUser.value.trim(),
                    password: pwdPass.value.trim()
                })
            })
            .then(r => r.json())
            .then(res => {
                if (res.code !== 200) throw new Error(res.message);
                onLoginSuccess(res.data);
            })
            .catch(err => {
                showMsg(err.message || '登录失败', 'error');
                pwdBtn.disabled = false; pwdBtn.textContent = '登录';
            });
        });

        // ===== 短信登录 =====
        const smsPhone = document.getElementById('glmSmsPhone');
        const smsCode = document.getElementById('glmSmsCode');
        const sendBtn = document.getElementById('glmSendBtn');
        const smsBtn = document.getElementById('glmSmsLoginBtn');
        let countdown = 0, timer = null;

        function checkSend() {
            const v = /^1[3-9]\d{9}$/.test(smsPhone.value.trim());
            sendBtn.disabled = !(v && countdown === 0);
            sendBtn.textContent = countdown > 0 ? countdown + 's' : '发送验证码';
        }
        function checkSms() {
            const vp = /^1[3-9]\d{9}$/.test(smsPhone.value.trim());
            const vc = /^\d{6}$/.test(smsCode.value.trim());
            smsBtn.disabled = !(vp && vc);
        }
        smsPhone.addEventListener('input', () => { checkSend(); checkSms(); });
        smsCode.addEventListener('input', checkSms);

        // ★ 发送验证码：后端返回 code → alert 显示 → 自动填入
        sendBtn.addEventListener('click', () => {
            const phone = smsPhone.value.trim();
            if (!/^1[3-9]\d{9}$/.test(phone)) { showMsg('手机号格式错误', 'error'); return; }
            sendBtn.disabled = true;

            fetch('/api/user/send-sms', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ phone })
            })
            .then(r => r.json())
            .then(res => {
                if (res.code !== 200) throw new Error(res.message);
                const code = res.data;

                // ★ 弹窗显示验证码
                alert('【演示环境】\n您的验证码是：' + code + '\n\n已自动填入。');
                smsCode.value = code;
                checkSms();

                countdown = 60;
                checkSend();
                timer = setInterval(() => {
                    countdown--;
                    checkSend();
                    if (countdown <= 0) { clearInterval(timer); checkSend(); }
                }, 1000);
            })
            .catch(err => { showMsg(err.message || '发送失败', 'error'); sendBtn.disabled = false; });
        });

        smsBtn.addEventListener('click', () => {
            smsBtn.disabled = true; smsBtn.textContent = '登录中...';
            fetch('/api/user/login-sms', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({
                    phone: smsPhone.value.trim(),
                    code: smsCode.value.trim()
                })
            })
            .then(r => r.json())
            .then(res => {
                if (res.code !== 200) throw new Error(res.message);
                onLoginSuccess(res.data);
            })
            .catch(err => {
                showMsg(err.message || '登录失败', 'error');
                smsBtn.disabled = false; smsBtn.textContent = '登录 / 注册';
            });
        });

        modal.addEventListener('click', e => {
            if (e.target === modal) window.closeLoginModal();
        });
    }

    // ==================== 绑定账号弹窗 ====================
    window.openBindModal = function () {
        const modal = document.getElementById('bindAccountModal');
        if (!modal) { alert('绑定弹窗未加载'); return; }
        document.getElementById('bindUsername').value = '';
        document.getElementById('bindPassword').value = '';
        document.getElementById('bindConfirm').value = '';
        document.getElementById('bindMsg').className = 'glm-msg';
        modal.classList.add('show');
    };

    window.closeBindModal = function () {
        const modal = document.getElementById('bindAccountModal');
        if (modal) modal.classList.remove('show');
    };

    function bindBindModalEvents() {
        const modal = document.getElementById('bindAccountModal');
        if (!modal) return;
        const bindUser = document.getElementById('bindUsername');
        const bindPwd = document.getElementById('bindPassword');
        const bindConfirm = document.getElementById('bindConfirm');
        const bindMsg = document.getElementById('bindMsg');
        const bindBtn = document.getElementById('bindSubmitBtn');

        function showBindMsg(text, type) {
            bindMsg.textContent = text;
            bindMsg.className = 'glm-msg ' + (type || '');
        }

        bindBtn.addEventListener('click', function () {
            const username = bindUser.value.trim();
            const password = bindPwd.value.trim();
            const confirm = bindConfirm.value.trim();

            if (!username || username.length < 2 || username.length > 20) {
                showBindMsg('用户名长度 2~20 位', 'error'); return;
            }
            const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*._-])[A-Za-z\d!@#$%^&*._-]{8,20}$/;
            if (!PWD_REGEX.test(password)) {
                showBindMsg('密码必须包含大小写字母、数字、特殊字符，长度 8~20 位', 'error'); return;
            }
            if (password !== confirm) {
                showBindMsg('两次输入的密码不一致', 'error'); return;
            }

            bindBtn.disabled = true;
            bindBtn.textContent = '绑定中...';

            fetch('/api/user/bind-account', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ username, password })
            })
            .then(r => r.json())
            .then(res => {
                if (res.code !== 200) throw new Error(res.message);
                showBindMsg('绑定成功，正在刷新...', 'success');
                setTimeout(() => window.location.reload(), 800);
            })
            .catch(err => {
                showBindMsg(err.message || '绑定失败', 'error');
                bindBtn.disabled = false;
                bindBtn.textContent = '确认绑定';
            });
        });

        modal.addEventListener('click', e => {
            if (e.target === modal) window.closeBindModal();
        });
    }

    // ==================== 启动 ====================
    document.addEventListener('DOMContentLoaded', function () {
        window.refreshCurrentUser();
        bindLoginModalEvents();
        bindBindModalEvents();
    });
})();