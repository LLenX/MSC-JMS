var login_url = "/login";
var ui_url = "/ui";

var show_error = function(error){
	$(".alert").removeClass("alert-info")
	.addClass("alert-danger")
	.text(error);
}

var submit_button_prepare = function(){
	$("#submit").click(function(){
		console.log($("#login-form").serialize());
		$.post(login_url, $("#login-form").serialize())
		.done(function(){
			window.location(ui_url);
		}).fail(function(){
			show_error("用户名或密码错误！")
		})
	})
}

submit_button_prepare();

