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
    var checkboxPrepare = function(){
        //勾选了功能后，该行后面的选项才可选
        $(".checkbox input").on("click", function() {
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
    }
    
    var selectPrepare = function(){
        //what does select means : 选择框组
        //allSelectPrepare       : 每个select都有的行为
        //firstSelectPrepare     : 每行第一个select都有的行为
        //thirdSelectPrepare     : 每行第三个select都有的行为
        allSelectPrepare();
        firstSelectPrepare();
        ThirdSelectPrepare();
    }

    var allSelectPrepare = function(){
        //每一行前面的选完了后面才可选
        $("select").on("change", function() {
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
    }
    
    var firstSelectPrepare = function(){
        //第一个选项确定时,在第三个选项添加相应的option
        $(".form-group:nth-of-type(1)").find("select").on("change", function() {
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
    }
    

    var ThirdSelectPrepare = function(){
        //第三个选项选定时,在第四个选项添加相应的option
        $(".form-group:nth-of-type(3)").find("select").on("change", function() {
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
    }
    

    var uploadParamButtonPrepare = function(){
        //"提交选择"被点击时
        //若用户的勾选合法，则上传参数
        //否则提醒用户修改
        $("#choices").click(function() {
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
    }
    

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
        //若三个checkbox都不勾选，则也返回false
        var first_checkbox_is_checked  = $("#first-line .checkbox input").is(":checked");
        var second_checkbox_is_checked = $("#second-line .checkbox input").is(":checked");
        var third_checkbox_is_checked  = $("#relation").is(":checked");
        if (!first_checkbox_is_checked
        &&  !second_checkbox_is_checked
        &&  !third_checkbox_is_checked)
            return false; 
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
        //上传choices, choices存有各选项的选择。
        //点击后提交按钮失效，直到服务器相应后才可再点击
        //成功就使"提交修改"按钮可点击,
        //		添加一个可供下载的链接,
        //      打开有修改界面的网页。
        //失败就在控制台显示"fail"。
        $("#tip").text("正在上传report,请耐心等候");
        $("#choices").attr("disabled", "disabled");
        $.ajax({
            type: "post",
            url: target_url_param,
            data: choices,
        }).done(function (res) {
            uploadConnectSucceed(res);
        }).fail(function (res) {
            uploadParamConnectFail();
        });
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
    var uploadConnectSucceed = function(res){
        //若三项功能中某项出错，则把其错误信息显示在"#error-message"上
        //再让"提交选择"按钮可点击
        var $error_message = $("#error-message").children().remove();

        if (res.predict.success == false){
            var $predict_message = $("<div></div>").attr("id", "predict-message");
            $("<p></p>").text("预测出错！错误信息:").appendTo($predict_message);
            for (var error in res.predict.message){
                $("<p></p>").text(error).appendTo($predict_message);
            }
            $predict_message.appendTo($error_message);
        }

        if (res.check.success == false){
            var $check_message = $("<div></div>").attr("id", "check-message");
            $("<p></p>").text("检测出错！错误信息:").appendTo($check_message);
            for (var error in res.check.message){
                $("<p></p>").text(error).appendTo($check_message);
            }
            $check_message.appendTo($error_message);
        }

        if (res.analyze.success == false){
            var $analyze_message = $("<div></div>").attr("id", "analyze-message");
            $("<p></p>").text("检测出错！错误信息:").appendTo($analyze_message);
            for (var error in res.analyze.message){
                $("<p></p>").text(error).appendTo($analyze_message);
            }
            $analyze_message.appendTo($error_message);
        }

        if (res.predict.success
        &&  res.check.success
        &&  res.analyze.success)
            uploadParamSucceed();
        
        $("#choices").removeAttr("disabled");
    }
    var uploadParamSucceed = function(){
        $("#return-revise").removeAttr("disabled");
        addDownloadButton();
        window.open(target_url_revise);
        $("#tip").text("计算完成，请点击下载按钮下载，并可在弹出的修改页面中修改。若无弹出可能是窗口被拦截，请解除拦截");
    }

    var uploadParamConnectFail = function(){
        console.log("fail");
        $("#tip").text("与服务器连接出错");
    }

    var addDownloadButton = function(){
        //添加一个可供下载的链接， 如果已存在就不添加
        if ($("#download-button").length > 0)
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
    
    
    $("#revise").hide(); //暂时不需要再此页面有修改界面
    checkboxPrepare();
    selectPrepare();
    uploadParamButtonPrepare();
    setAllSelectToDisabled();
})
