$(function(){
    var target_url_revise = "/revise";

    var reviseButtonPrepare = function(){
        $("#revise").on("submit", function(event){
            //re            : 正则表达式，匹配整数或者小数
            //pass_re_check : 是否每项都通过re的匹配
            var re = /^\d+(.\d+)?$/;
            var pass_re_check;

            event.preventDefault();

            $("#revise > div").find("input").each(function(i, element){
                //有一项不匹配re就中断
                if (!re.test($(element).val())){
                    pass_re_check = false;
                    return false;
                }
            });

            if (!pass_re_check){
                //不通过re匹配则返回
                $("#tip").text("每个月份都应填上整数或小数");
                return;
            }
            else postRevisedData();
        })
    }


    var postRevisedData = function(){
        //若成功上传修改的数据则让"提交修改"按钮不可点击
        //否则在控制台显示"连接失败"
        $.ajax({
            type: "post",
            url: target_url_revise,
            data: new FormData(this)
        }).done(function(res){
            if (res != "fail"){
                postRevisedDataSucceed();
            }
        }).fail(function(){
            console.log("连接失败");
        })
    }

    var postRevisedDataSucceed = function(){
        //让"提交修改"按钮不可点击
        $("#revise-button").attr("disabled", "disabled");
        addDownloadButton();
    }

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

    reviseButtonPrepare();
})