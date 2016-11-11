$(windowOnLoad);

function windowOnLoad(){
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


    allSelectsPrepare();
    checkboxButtonPrepare();
    uploadParamButtonPrepare();
    function checkboxButtonPrepare(){
        $("input[type='checkbox']").on("change", function(){
            var $form = $(this).next().next();
            var $first_select = $form.find("select:first");
            var $all_select_in_the_row = $form.find("select");

            if ($(this).is(":checked"))
                 selectEnable($first_select);
            else selectDisable($all_select_in_the_row);
        })
    }
    function allSelectsPrepare(){
        setSelectsStyle();
        allSelectsOnSelected();
        firstSelectOnSelected();
        ThirdSelectOnSelected();
    }

    function setSelectsStyle(){
        $('.selectpicker').selectpicker({
            style: 'btn-info'
        });
    }

    function allSelectsOnSelected(){
        //每一行前面的选完了后面才可选
        $("select").on("change", function() {
            var $selected_option = $(this).find("option:selected");
            if ($selected_option.text() == "请选择") {
                var $selects_after = $(this).parent().nextAll().children("select");
                selectDisable($select_after);
            }
            else {
                $select_after = $(this).parent().next().children("select");
                selectEnable($select_after);
            }
        });
    }

    function firstSelectOnSelected(){
        $(".btn-group select").on("change", function(){
            var $selected_option = $(this).find("option:selected");
            var $text_of_selected_option = $selected_option.text();
            var $next_btn_group = $(this).parent().next().next();
            var $third_select = $next_btn_group.find("select");
            var $fourth_select = $next_btn_group.next().find("select");
            var appendOptionForThirdSelect = appendOption.bind($third_select);

            selectDisable($fourth_select);
            $third_select.children().remove();
            appendOptionForThirdSelect("请选择");

            if ($text_of_selected_option == "全社会用电量") {
                appendOptionForThirdSelect("年度");
                appendOptionForThirdSelect("半年度");
                appendOptionForThirdSelect("季度");

            }
            else if ($text_of_selected_option == "分镇街用电量") {
                appendOptionForThirdSelect("年度");
                appendOptionForThirdSelect("半年度");
            }
            else if ($text_of_selected_option == "副模型") {
                appendOptionForThirdSelect("年度");
            }
            $third_select.selectpicker("refresh");
        })
    }

    function ThirdSelectOnSelected(){
        $(".btn-group:nth-of-type(3) select").on("change", function(){
            var $selected_option = $(this).find("option:selected");
            var $text_of_selected_option = $selected_option.text();
            var $fourth_select = $(this).parent().next().find("select");
            var appendOptionForFourthSelect = appendOption.bind($fourth_select);

            $fourth_select.children().remove();
            appendOptionForFourthSelect("请选择");
            if ($text_of_selected_option == "年度")
                appendOptionForFourthSelect("全年");
            else if ($text_of_selected_option == "半年度"){
                appendOptionForFourthSelect("上半年");
                appendOptionForFourthSelect("下半年");
            }
            else if ($text_of_selected_option == "季度"){
                appendOptionForFourthSelect("第一季度");
                appendOptionForFourthSelect("第二季度");
                appendOptionForFourthSelect("第三季度");
                appendOptionForFourthSelect("第四季度");
            }
            $fourth_select.selectpicker("refresh");
        })
    }

    function uploadParamButtonPrepare(){
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
                $("#tip").text("有的选项没勾选，请检查。checkbox被勾选后，那一行的每项都应被选择")
                .attr("class", "alert alert-warning");
            }
        });
    } 

    function appendChoicesToObjectChoices(){
        //choices的key的含义在文件开头可找
        choices["00"] = $("#first-line input").is(":checked");
        $.each($("#first-line option:selected"), function (i, option) {
            choices["0" + (i + 1)] = $(option).text();
        });

        choices["10"] = $("#second-line input").is(":checked");
        $.each($("#second-line option:selected"), function (i, option) {
            choices["1" + (i + 1)] = $(option).text();
        });

        choices["20"] = $("#analyze").is(":checked");  
    }

    function checkIfAllOptionsAreSelected(){
        //若某行第一个checkbox被勾选，则该行每个选项都应被选择
        //若三个checkbox都不勾选，则也返回false
        var first_checkbox_is_checked  = $("#first-line input[type='checkbox']").is(":checked");
        var second_checkbox_is_checked = $("#second-line input[type='checkbox']").is(":checked");
        var third_checkbox_is_checked  = $("#analyze").is(":checked");
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
    }

    function uploadParam(){
        //上传choices, choices存有各选项的选择。
        //点击后提交按钮失效，直到服务器相应后才可再点击
        //成功就使"提交修改"按钮可点击,
        //		添加一个可供下载的链接,
        //      打开有修改界面的网页。
        //失败就在控制台显示"fail"。
        $("#tip").text("正在上传report,请耐心等候")
        .attr("class", "alert alert-info");
        selectDisable($("#choices"));
        $.ajax({
            type: "post",
            url: target_url_param,
            data: choices,
        }).done(function (res) {
            uploadConnectSucceed(res);
            selectEnable($("#choices"));
        }).fail(function (res) {
            uploadParamConnectFail();
            selectEnable($("#choices"));
        });
    }

    function uploadConnectSucceed(res){
        //若三项功能中某项出错，则把其错误信息显示在"#error-message"上
        res = JSON.parse(res);
        console.log(res);
        var $error_message = $("#error-message");

        $error_message.children().remove();

        if (res.predict && res.predict.success == false){
            var $predict_message = $("<div></div>").attr("id", "predict-message");
            $("<p></p>").text("预测出错！错误信息:").appendTo($predict_message);
            for (var error in res.predict.message){
                $("<p></p>").text(error + res.predict.message[error]).appendTo($predict_message);
            }
            $predict_message.appendTo($error_message);
        }

        if (res.check && res.check.success == false){
            var $check_message = $("<div></div>").attr("id", "check-message");
            $("<p></p>").text("检测出错！错误信息:").appendTo($check_message);
            for (var error in res.check.message){
                $("<p></p>").text(error + res.check.message[error]).appendTo($check_message);
            }
            $check_message.appendTo($error_message);
        }

        if (res.analyze && res.analyze.success == false){
            var $analyze_message = $("<div></div>").attr("id", "analyze-message");
            $("<p></p>").text("检测出错！错误信息:").appendTo($analyze_message);
            for (var error in res.analyze.message){
                $("<p></p>").text(error + res.analyze.message[error]).appendTo($analyze_message);
            }
            $analyze_message.appendTo($error_message);
        }
        if (res.predict && res.predict.success == false 
        ||  res.check  && res.check.success   == false
        ||  res.analyze  && res.analyze.success == false){
            $("#tip").text("计算失败.")
            .attr("class", "alert alert-danger");
        }
        else uploadParamSucceed();
        
    }

    function uploadParamSucceed(){
        addDownloadButton();
        window.open(target_url_revise);
        $("#tip").text("计算完成，请点击下载按钮下载，并可在弹出的修改页面中修改。若无弹出可能是窗口被拦截，请解除拦截")
        .attr("class", "alert alert-success");
    }

    function uploadParamConnectFail(){
        console.log("fail");
        $("#tip").text("与服务器连接出错")
        .attr("class", "alert alert-danger");
    }

    function addDownloadButton(){
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
        .appendTo(".panel-body");
    }
    //以下为功能性函数

    function selectEnable($select){
        $select.removeAttr("disabled").selectpicker("refresh");
    }

    function selectDisable($select){
        $select.attr("disabled", "disabled").selectpicker("refresh");
    }

    function appendOption(str){
        var $new_option = $("<option></option>").text(str);
        this.append($new_option);
    }
}