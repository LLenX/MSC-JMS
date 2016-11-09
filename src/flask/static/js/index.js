$(function () {
	//target_url_param  : 提交参数的url
	//target_url_revise : 提交专家修改后的数据的url，暂时不需要
	//target_url_report : 提交report的url
	//choices 			: 提交参数时，该对象包含各个选项的选择
	//revise_data 		: 提交数据修改时，该对象包含各修改后的数据，暂时不需要
	//file_collection   : 包含要回传的report.doc，暂时不需要
	
	//choices的key的解释:
	//第一个数字i代表第i行
	//第二个数字j代表第j列
	//choices["00"]指"预测"这个checkbox
	//choices["11"]指"精度检验"那行下的"年度"
	//choices["20"]指"关联性分析"这个checkbox
    var file_collection = [];
    var target_url_param = "/upload_param";
    var target_url_revise = "/upload_revise";
	var target_url_report = "/download_report";
    var choices = {};
    var revise_data = {};

    $(".checkbox input").on("click", function() {
		//勾选了功能后，该行后面的选项才可选
        $select = $(this).parent("label").parent("div").next("form");
        if ($(this).is(":checked"))
            $select.find("select:first").removeAttr("disabled");
        else {
            $the_first_option = $select.find("select").find("option:first");
            $the_first_option.attr("selected", true);
            $the_first_option.nextAll("option").attr("selected", false);
            $select.find("select").attr("disabled", "disabled");
        }
    });

    $("select").on("change", function() {
		//每一行前面的选完了后面才可选
        $selected_option = $(this).find("option:selected");
        if ($selected_option.text() == "请选择") {
            $selects_after = $(this).parent("div").parent("div").nextAll("div").find("select");
            $selects_after.attr("disabled", "disabled");
        }
        else {
            $select_after = $(this).parent("div").parent("div").next("div").find("select");
            $select_after.removeAttr("disabled");
        }
    });

    $(".form-group:nth-of-type(1)").find("select").on("change", function() {
		//第一个选项确定时,在第三个选项添加相应的option
        $selected_option = $(this).find("option:selected");
        $text_of_the_selected_option = $selected_option.text();
        $third_select = $(this).parent("div").parent("div").next().next().find("select");
        $fourth_select = $(this).parent("div").parent("div").next().next().next().find("select");
        $fourth_select.attr("disabled", "disabled");
        var appendOptionForThirdSelect = appendOption.bind(window, $third_select);
        if ($text_of_the_selected_option != "请选择")
            $($third_select).children().remove();
        appendOptionForThirdSelect("请选择");
        appendOptionForThirdSelect("年度");

        if ($text_of_the_selected_option == "全社会用电量") {
            appendOptionForThirdSelect("半年度");
            appendOptionForThirdSelect("季度");

        }
        else if ($text_of_the_selected_option == "分镇街用电量") {
            appendOptionForThirdSelect("半年度");
        }
        else if ($text_of_the_selected_option == "副模型") {
            //不添加option
        }
    });


    $(".form-group:nth-of-type(3)").find("select").on("change", function() {
		//第三个选项选定时,在第四个选项添加相应的option
        $selected_option = $(this).find("option:selected");
        $text_of_the_selected_option = $selected_option.text();
        $fourth_select = $(this).parent("div").parent("div").next().find("select");
        var appendOptionForFourthSelect = appendOption.bind(window, $fourth_select);
        if ($text_of_the_selected_option != "请选择")
            $($fourth_select).children().remove();
        if ($text_of_the_selected_option == "年度") {
            appendOptionForFourthSelect("全年");
        }
        else if ($text_of_the_selected_option == "半年度") {
            appendOptionForFourthSelect("请选择");
            appendOptionForFourthSelect("上半年");
            appendOptionForFourthSelect("下半年");
        }
        else if ($text_of_the_selected_option == "季度") {
            appendOptionForFourthSelect("请选择");
            appendOptionForFourthSelect("第一季度");
            appendOptionForFourthSelect("第二季度");
            appendOptionForFourthSelect("第三季度");
            appendOptionForFourthSelect("第四季度");
        }
    });


    $("#choices").click(function() {
		//"提交选择"被点击时
		//若用户的勾选合法，则上传参数
		//否则提醒用户修改
        choices = {};
        appendChoicesToObjectChoices(choices);
        var all_options_are_selected = checkIfAllOptionsAreSelected();
        if (all_options_are_selected){
            uploadParam();
        }
        else {
            $("#tip").text("有的选项没勾选，请检查。checkbox被勾选后，那一行的每项都应被选择");
        }
        
    });

    $("#revise-button").click(function(){
		//暂时无用
        $("#revise-form .input-group").each(function(i, element){
            var month_index_str = $(element).find("span").text()[0];
            var $input = $(element).find("input");
            var number_in_month_str = $input.val() != "" ? $input.val() : $input.attr("placeholder");
            var number_in_month = parseFloat(number_in_month_str);
            revise_data[month_index_str] = number_in_month;
        });
        $.ajax({
			type: "post",
			url: target_url_revise,
			data: revise_data,
		}).done(function (res) {
			if (res != "fail") {
				$("#revise").hide();
				$("#tip").text("已提交修改");
			}
		}).fail(function (res) {
			console.log("fail");
		});
    });

    var setAllSelectToDisabled = function() {
		//在页面一开始让所有select无法选择
        $all_selects = $("select");
        $all_selects.attr("disabled", "disabled");
    }

    var appendOption = function($select, str) {
		//用于添加一个option, 不存在才添加
        $option = $("<option></option>").text(str);
        if ($select.find("option:contains(" + str + ")").length == 0)
            $select.append($option);
    }

    var appendChoicesToObjectChoices = function(choices) {
		//choices的key的含义在文件开头可找
        choices["00"] = $("#first-line input").is(":checked");
        $.each($("#first-line option:selected"), function (i, option) {
            choices["0" + (i + 1)] = $(option).text();
        });

        choices["10"] = $("#second-line input").is(":checked");
        $.each($("#second-line option:selected"), function (i, option) {
            choices["1" + (i + 1)] = $(option).text();
        });

        choices["20"] = $("#relation").is(":checked");  
        console.log(choices);
    }

    var checkIfAllOptionsAreSelected = function(){
		//若某行第一个checkbox被勾选，则该行每个选项都应被选择
        var first_checkbox_is_checked = $("#first-line .checkbox input").is(":checked");
        var second_checkbox_is_checked = $("#second-line .checkbox input").is(":checked");
        if (first_checkbox_is_checked){
            var options = $("#first-line option:selected");
            for(var i = 0; i < options.length; i++){
                if ($(options[i]).text() == "请选择")
                    return false;
            }
        }
        if (second_checkbox_is_checked){
            var options = $("#second-line option:selected");
            for(var i = 0; i < options.length; i++){
                if ($(options[i]).text() == "请选择")
                    return false;
            }
        }
        return true;
    };
	
	var uploadParam = function(){
		//上传choices, choices存有各选项的选择
		//成功就使"提交修改"按钮可点击,
		//		添加一个可供下载的链接
		//失败就在控制台显示"fail"
		$("#tip").text("正在上传report");
		$.ajax({
			type: "post",
			url: target_url_param,
			data: choices,
		}).done(function (res) {
			if (res != "fail") {
				$("#return-revise").removeAttr("disabled");
				addDownloadButton();
			}
		}).fail(function (res) {
			console.log("fail");
		});
        $("#tip").text("执行完上传操作");
	}

    var appendInputGroup = function(res){
		var $revise_form = $("#revise-form");
		for(var i = 1; i <= 12; i++){
			if (res[i.toString()] != undefined){
				var $input_group = $("<div></div>").addClass("input-group");
				var $span = $("<span></span>")
							.addClass("input-group-addon")
							.text(i + "月");
				var $input = $("<input></input>")
							.attr({
								"class" : "form-control",
								"type" : "text",
								"placeholder" : res[i.toString()]
							});
				$input_group.append($span).append($input);
				$revise_form.append($input_group);
			}
		}
    };
	var addDownloadButton = function(){
		//添加一个可供下载的链接， 如果已存在就不添加
		if ($("#download").length > 0)
			return;
		$("<a></a>")
		.attr({
			"href"		: target_url_report,
			"download" 	: "reports.zip",
			"id"		: "download-button"
		})
		.text("点击下载report")
		.click(function(){
			$(this).remove();
		})
		.appendTo("body");
	};
	
	
    $("#revise").hide();
    setAllSelectToDisabled();
})
