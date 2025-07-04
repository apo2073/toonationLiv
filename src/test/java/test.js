"use strict";

window.host = function(){};

window.host.https = function () {
    return 'https://toon.at';
};

window.host.ws = function () {
    return 'wss://ws.toon.at';
};

window.host.ws_check = function () {
    return 'https://ws.toon.at/ping';
};

window.host.chat = function () {
    return 'wss://chat.toon.at:8072';
};

window.host.chat_check = function () {
    return 'https://chat.toon.at:8072/ping';
};

window.host.asset_root = function () {
    //return 'https://toothcdn.xyz/assets';
    return 'https://sbosirdwzbyw9257399.gcdn.ntruss.com/assets';
}

window.host.template_root = function () {
    //return '/template';
    return 'https://toothcdn.xyz:8432/toonation/template';
}

window.host.dist_root = function () {
    //return 'https://toothcdn.xyz/assets/toonation';
    return 'https://sbosirdwzbyw9257399.gcdn.ntruss.com/assets/toonation';
};

window.host.proxy = function () {
    return 'https://toothcdn.xyz/proxy?p=';
}

window.host.upload = function() {
    return 'https://toothcdn.xyz:8432/uploaded'
}

window.host.payment_url = function() {
    return "https://payment.toothlife.co.kr";
}

window.host.api_server = function () {
    return 'https://api.toon.at/integration';
}

window.host.live_streaming_server = function () {
    return 'https://live-streaming.toon.at';
}

window.host.api_server_host = function () {
    return 'https://api.toon.at';
}