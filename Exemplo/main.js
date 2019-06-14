$(document).ready(function () {

	var sendMessageBtn 	= $('#send-message-button');
	var inputConType 	= $('#input-contype');
	var inputAddress 	= $('#input-address');
	var inputAddrPort 	= $('#input-addrport');
	
	var responseElem 	= $('#response');
	
	/**
	 * Send message operation
	 */
	sendMessageBtn.click(function () {
		var request = {};
		request.contype = inputConType.val();
		request.address = inputAddress.val();
		request.addrport = inputAddrPort.val();

		var event = new CustomEvent("send-message-event", {
			detail: {
				data: request
			},
			bubbles: true,
			cancelable: true
		});
		document.dispatchEvent(event);
	});
			
	/**
	 * Get message event listener
	 */
	document.addEventListener("get-message-event", function (data) {
		var responseObject = data.detail.data;
		responseElem.text(responseObject.message);
	});
});
