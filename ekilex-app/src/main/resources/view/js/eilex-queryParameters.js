class QueryParamsService {
  parseParams() {
    const search = window.location.search;
    const query = search.substring(search.indexOf('?') + 1);

    var re = /([^&=]+)=?([^&]*)/g;
    var decodeRE = /\+/g;

    var decode = function (str) {
        return decodeURIComponent(str.replace(decodeRE, " "));
    };

    var params = {}, e;
    while (e = re.exec(query)) {
        var k = decode(e[1]), v = decode(e[2]);
        if (k.substring(k.length - 2) === '[]') {
            k = k.substring(0, k.length - 2);
            (params[k] || (params[k] = [])).push(v);
        }
        else params[k] = v;
    }

    var assign = function (obj, keyPath, value) {
        var lastKeyIndex = keyPath.length - 1;
        for (var i = 0; i < lastKeyIndex; ++i) {
            var key = keyPath[i];
            if (!(key in obj))
                obj[key] = {}
            obj = obj[key];
        }
        obj[keyPath[lastKeyIndex]] = value;
    }

    for (var prop in params) {
        var structure = prop.split('[');
        if (structure.length > 1) {
            var levels = [];
            structure.forEach(function (item, i) {
                var key = item.replace(/[?[\]\\ ]/g, '');
                levels.push(key);
            });
            assign(params, levels, params[prop]);
            delete(params[prop]);
        }
    }
    return params;
  }

  generateSearch(params) {
    return Object.keys(params).map((item) => {
      return `${item}=${params[item]}`;
    }).join('&');
  }

  get(key) {
    const params = this.parseParams();
    return params[key];
  }
  
  set(key, value) {
    const params = this.parseParams();
    params[key] = value;
    const search = this.generateSearch(params);
    window.history.replaceState(null, null, `?${search}`);
  }

  delete(key) {
    const params = this.parseParams();
    delete params[key];
    const search = this.generateSearch(params);
    window.history.replaceState(null, null, `?${search}`);
  }
}

const QueryParams = new QueryParamsService();