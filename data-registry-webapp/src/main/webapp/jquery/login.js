$(document).ready(function() {
    getLoginLink();
});
function getLoginLink() {
    $(".signin").click(function(e) {
        e.preventDefault();
        $('#login-error').html('');
        $("fieldset#signin_menu").toggle();
        $(".signin").toggleClass("menu-open");
    });
    $(".browse").mouseover(function() {
        $(".browse").addClass("menu-open");
        $("fieldset#browse_menu").fadeIn(400);
    });
    $("#browse_menu").mouseout(function() {
        $(".browse").removeClass("menu-open");
    });

    $(".signout").click(function(e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/logout',
            success: function(data) {
                $('#signin-link').html('Sign in');
                $('a#signin-link').attr('href', '#');
                $('a#signin-link').attr('class', 'signin');
                getLoginLink();
            }
        });

    });
    $("fieldset#signin_menu").mouseup(function() {
        return false
    });
    $(document).mouseup(function(e) {
        if ($(e.target).parent("a.signin").length == 0) {
            $(".signin").removeClass("menu-open");
            $("fieldset#signin_menu").fadeOut(400);
        }
        if ($(e.target).parent("a.browse").length == 0) {
            $(".browse").removeClass("menu-open");
            $("fieldset#browse_menu").fadeOut(400);
        }
    });
}
function login() {
    var username = $('#username').val();
    var password = $('#password').val();
    $.ajax({
        type: 'POST',
        url: '/login',
        data: 'username=' + username + '&password=' + password,
        success: function(data) {
            $('#signin-link').html('Sign out');
            $('a#signin-link').attr('href', '/logout');
            $('a#signin-link').attr('class', 'signout');
            $(".signin").removeClass("menu-open");
            $("fieldset#signin_menu").hide();
            $(".signout").click(function(e) {
                e.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '/logout',
                    success: function(data) {
                        $('#signin-link').html('Sign in');
                        $('a#signin-link').attr('href', '#');
                        $('a#signin-link').attr('class', 'signin');
                        getLoginLink();
                    }
                });

            });
        },
        error: function(xhr, textStatus, errorThrown) {
            $('#login-error').html('Authentication Failed: incorrect username or password');
        }
    });
    return false;
}