$(function(){
    var file_collection = [];
    var target_url_file = "/upload_file";
    var target_url_param = "/upload_param";

    $(".checkbox input").on("click", function(){
        $select = $(this).parent("label").parent("div").next("form");
        if($(this).is(":checked"))
            $select.find("select:first").removeAttr("disabled");
        else{
            $the_first_option = $select.find("select").find("option:first");
            $the_first_option.attr("selected", true);
            $the_first_option.nextAll("option").attr("selected", false);
            $select.find("select").attr("disabled", "disabled");
        }
            
    });

    $("select").on("change", function(){
        $selected_option = $(this).find("option:selected");
        if ($selected_option.text() == "请选择"){
            $selects_after = $(this).parent("div").parent("div").nextAll("div").find("select");
            $selects_after.attr("disabled", "disabled");
        }
        else{
            $select_after = $(this).parent("div").parent("div").next("div").find("select");
            $select_after.removeAttr("disabled");
        } 
    });

    $(".form-group:nth-of-type(3)").find("select").on("change", function(){
        $selected_option = $(this).find("option:selected");
        $text_of_the_selected_option = $selected_option.text();
        $fourth_select = $(this).parent("div").parent("div").next().find("select");
        var appendOptionForFourthSelect = appendOption.bind(window, $fourth_select);
        if ($text_of_the_selected_option != "请选择")
            $($fourth_select).children().remove();
        if($text_of_the_selected_option == "年度"){
             appendOptionForFourthSelect("全年");
        }
        else if($text_of_the_selected_option == "半年度"){
            appendOptionForFourthSelect("请选择");
            appendOptionForFourthSelect("上半年");
            appendOptionForFourthSelect("下半年");
        }
        else if($text_of_the_selected_option == "季度"){
            appendOptionForFourthSelect("请选择");
            appendOptionForFourthSelect("第一季度");
            appendOptionForFourthSelect("第二季度");
            appendOptionForFourthSelect("第三季度");
            appendOptionForFourthSelect("第四季度");
        }
    });


    $("#choices").click(function(){
        var choices = {};
        appendChoicesToObjectChoices(choices);

        $.ajax({
            type: "post",
            url: target_url_param,
            data: choices,
        }).done(function(res){
            if (res != "fail"){
                window.open(res);
                $("#return-report").removeAttr("disabled");
                $("#choose-report").removeAttr("disabled");
                $("#msg").text("请修改report并回传");
            }
        }).fail(function(res){
            console.log("fail");
        });
    });

    $("#choose-report").click(function(){
        $('#report').click();
    });

    $("#return-report").click(function(){
        $.ajax({
            type: "post",
            url: target_url_file,
            data: new FormData($("#report")[0]),
            processData: false,
            contentType: false
        }).done(function(res){
            if (res != "fail"){
                 $("#return-report").attr("disabled", "disabled");
                 $("#choose-report").attr("disabled", "disabled");
                 $("#msg").text("report已上传成功");
            }
               
        }).fail(function(res){
            $("#msg").text("上传report失败");
        });
    });


    var setAllSelectToDisabled = function(){
        $all_selects = $("select");
        $all_selects.attr("disabled", "disabled");
    }

    var appendOption = function($select, str){
        $option = $("<option></option>").text(str);
        $select.append($option);
    }

    var appendChoicesToObjectChoices= function(choices){
        choices["00"] = $("#first-line input").is(":checked");//first-line
        $.each($("#first-line option:selected"), function(i, option){
            choices["0" + (i + 1)] = $(option).text();
        });

        choices["10"] = $("#second-line input").is(":checked");//second-line
        $.each($("#second-line option:selected"), function(i, option){
            choices["1" + (i + 1)] = $(option).text();
        });

        choices["20"] = $("#relation").is(":checked");  //relation
        console.log(choices);
    }

    setAllSelectToDisabled();
})