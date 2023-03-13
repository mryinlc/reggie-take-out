function sendMsg(params) {
    return $axios({
        'url': '/user/code',
        'method': 'get',
        params
    })
}

function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

  