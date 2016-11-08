$(function () {
    var file_collection = [];
    var target_url_file = "/upload_file";
    var target_url_param = "/upload_param";

    $(".checkbox input").on("click", function () {//勾选了功能后，后面的选项才可选
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

    $("select").on("change", function () {//前面的选完了后面才可选
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

    $(".form-group:nth-of-type(1)").find("select").on("change", function () {//第一个form-group决定第三个的选项的内容
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
            //do nothing
        }
    });


    $(".form-group:nth-of-type(3)").find("select").on("change", function () {//选定第三列会确定第四列内容
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


    $("#choices").click(function () {//提交params
        var choices = {};
        appendChoicesToObjectChoices(choices);
        var all_options_are_selected = checkIfAllOptionsAreSelected();
        if (all_options_are_selected){
            $("#tip").text("正在上传report");
            $.ajax({
                type: "post",
                url: target_url_param,
                data: choices,
            }).done(function (res) {
                if (res != "fail") {
                    $("#return-revise").removeAttr("disabled");
                    $("#msg").text("请修改report并回传");
                }
            }).fail(function (res) {
                console.log("fail");
            });
            $("#tip").text("执行完上传操作");
        }
        else {
            $("#tip").text("有的选项没有被选择，请检查一遍");
        }
        
    });

    $("#revise-button").click(function(){
        $("#return-revise").click();
    })


    var setAllSelectToDisabled = function () {//在页面一开始把所有select无效化
        $all_selects = $("select");
        $all_selects.attr("disabled", "disabled");
    }

    var appendOption = function ($select, str) {//用于添加一个option
        $option = $("<option></option>").text(str);
        if ($select.find("option:contains(" + str + ")").length == 0)//不存在才添加
            $select.append($option);
    }

    var appendChoicesToObjectChoices = function (choices) {
        choices["00"] = $("#first-line input").is(":checked");//first-line
        $.each($("#first-line option:selected"), function (i, option) {
            choices["0" + (i + 1)] = $(option).text();
        });

        choices["10"] = $("#second-line input").is(":checked");//second-line
        $.each($("#second-line option:selected"), function (i, option) {
            choices["1" + (i + 1)] = $(option).text();
        });

        choices["20"] = $("#relation").is(":checked");  //relation
        console.log(choices);
    }

    var checkIfAllOptionsAreSelected = function(){
        var first_checkbox_is_checked = $("#first-line .checkbox input").is(":checked");
        var second_checkbox_is_checked = $("#second-line .checkbox input").is(":checked");
        if (!first_checkbox_is_checked && !second_checkbox_is_checked)
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

    setAllSelectToDisabled();
})
