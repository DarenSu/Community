$(function(){
	$("#publishBtn").click(publish);
});
// 上面定义好了，单击时调用下面的function publish方法
function publish() {
	$("#publishModal").modal("hide");	// 先隐藏起来
	// 当点击新帖发布里面的发布的时候，将填写数据的框隐藏掉，然后先不急着去显示提示框，
	// 得先向服务器发送消息才，收到了结果才能提示的
	// 所以说得先发送请求，发送请求之前得要想获取帖子的标题和内容才行的
	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	/**
	 * 发送异步请求（POST）
	 * 参数：
	 * 	请求路径
	 * 	请求携带数据
	 * 	回调函数处理请求的响应
	 */
	$.post(
		CONTEXT_PATH + "/discuss/add",//路径
		{"title":title,"content":content},// 内容
		function (data) {// 回调函数处理处理返回结果
			data = $.parseJSON(data);// 返回结果是字符串需要转成字符串

			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框  (提示框默认不显示)
			$("#hintModal").modal("show");
			// 2秒后，自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果成功，刷新页面
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}