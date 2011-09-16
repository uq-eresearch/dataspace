$(document).ready(function() {
    getLoginLink();
});


function getLoginLink() {
	var login = function(e) {
		e.preventDefault();
	    var username = $('#username').val();
	    var password = $('#password').val();
	    var doLogin = function(url, secureRedirect) {
	        $.ajax({
	            type: 'POST',
	            url: url,
	            data: {
	            	'username': username,
	            	'password': password
	            },
	            xhrFields: {
	                withCredentials: true
	             },
	            crossDomain: jQuery.support.cors,
	            success: function(data) {
	            	if (secureRedirect) {
	            		window.location.href = _.template(
	            				'https://<%=hostname+pathname%>',
	                			window.location)
	            	} else {
	            		window.location.reload();
	            	}
	            },
	            error: function(xhr, textStatus, errorThrown) {
	                $('#login-error').html('Authentication Failed: incorrect username or password');
	            }
	        });
	    }

	    // Try for a secure login
	    var postUrl = '/login';
	    if (window.location.protocol != 'https:') {
	    	var securePostUrl = 'https://'+window.location.hostname+postUrl;
	    	$.ajax({
	            type: 'HEAD',
	            url: securePostUrl,
	            success: function() {
	            	doLogin(securePostUrl, true);
	            },
	            error: function() {
	            	doLogin(postUrl, false);
	            }
	    	});
	    } else {
	    	doLogin(postUrl, false);
	    }

	    return false;
	}

    $(".signin").click(function(e) {
        e.preventDefault();
        if ($('#login-dialog').length == 0) {
        	var dialog = $('<div id="login-dialog"></div>');
        	_.each(['username','password'], function(field) {
        		var capitalize = function(string) {
        			return string.charAt(0).toUpperCase()+string.slice(1);
        		}
        		var wrapper = $('<dl />');
        		var input = field == 'password' ?
        				$('<input type="password"/>') : $('<input />');
        		$('<dt><label/></dt><dd></dd>').appendTo(wrapper);
        		input.attr('id', field).keydown(function(e){
        			if (e.keyCode == '13') {
        				login(e);
        			}
        		});
        		$('dd', wrapper).append(input);
        		$('label', wrapper).text(capitalize(field)).attr('for', field);
        		dialog.append(wrapper);
        	});
        	$('<div/>').attr('id', 'login-error').appendTo(dialog);
        	$('<button/>').text('UQ Sign In').click(login).appendTo(dialog);
        	$('body').append(dialog);
        	dialog.dialog({
        		autoOpen: false,
        		title: 'Sign In'
        	});
        }
        $('#login-dialog').dialog('open');
    });
    $(".signout").click(function(e) {
        e.preventDefault();
        $.ajax({
                    type: 'POST',
                    url: '/logout',
                    success: function(data) {
                        location.reload();
                    }
                });

    });
    $("fieldset#signin_menu").mouseup(function() {
        return false;
    });
    $(document).mouseup(function(e) {
        if ($(e.target).parent("a.signin").length == 0) {
            $(".signin").removeClass("menu-open");
            $("fieldset#signin_menu").fadeOut(400);
        }
    });
}