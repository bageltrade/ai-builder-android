package com.example.ui.preview

object PuterHtmlBundler {

    fun buildBundledHtml(
        htmlContent: String,
        cssContent: String,
        jsContent: String
    ): String {
        // Strip <link rel="stylesheet" href="styles.css"> and <script src="app.js"></script> if present to avoid broken requests
        var cleanedHtml = htmlContent
            .replace(Regex("""<link[^>]*href=["'][^"']*styles?\.css["'][^>]*>""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""<script[^>]*src=["'][^"']*app\.js["'][^>]*></script>""", RegexOption.IGNORE_CASE), "")

        val shimScript = """
<script>
  (function(){
    function sendLog(level, args) {
      try {
        var str = Array.from(args).map(function(item) {
          if (typeof item === 'object') {
            try { return JSON.stringify(item); } catch(e) { return String(item); }
          }
          return String(item);
        }).join(' ');
        if (window.PuterAndroidBridge && window.PuterAndroidBridge.logMessage) {
          window.PuterAndroidBridge.logMessage(level, str);
        }
      } catch(e) {}
    }
    var origLog = console.log, origWarn = console.warn, origErr = console.error;
    console.log = function() { sendLog('info', arguments); origLog.apply(console, arguments); };
    console.warn = function() { sendLog('warn', arguments); origWarn.apply(console, arguments); };
    console.error = function() { sendLog('error', arguments); origErr.apply(console, arguments); };
    window.onerror = function(msg, url, line) { sendLog('error', [msg + ' (Line ' + line + ')']); };
  })();

  // Puter.js SDK Compatibility Engine
  window.puter = window.puter || {
    auth: {
      isSignedIn: function() { return true; },
      getUser: function() { return { username: "puter_user", email: "user@puter.com" }; },
      signIn: function() { return Promise.resolve({ username: "puter_user" }); },
      signOut: function() { return Promise.resolve(); }
    },
    kv: {
      set: function(k, v) {
        try {
          var val = typeof v === 'string' ? v : JSON.stringify(v);
          localStorage.setItem('puter_kv_' + k, val);
          return Promise.resolve(true);
        } catch(e) { return Promise.reject(e); }
      },
      get: function(k) {
        try {
          var raw = localStorage.getItem('puter_kv_' + k);
          if (raw === null) return Promise.resolve(null);
          try { return Promise.resolve(JSON.parse(raw)); } catch(e) { return Promise.resolve(raw); }
        } catch(e) { return Promise.resolve(null); }
      },
      del: function(k) {
        try {
          localStorage.removeItem('puter_kv_' + k);
          return Promise.resolve(true);
        } catch(e) { return Promise.reject(e); }
      },
      list: function() {
        var keys = [];
        for (var i = 0; i < localStorage.length; i++) {
          var k = localStorage.key(i);
          if (k && k.indexOf('puter_kv_') === 0) {
            keys.push(k.substring(9));
          }
        }
        return Promise.resolve(keys);
      }
    },
    ui: {
      alert: function(msg) { alert(msg); return Promise.resolve(); }
    }
  };
</script>
""".trimIndent()

        val styleTag = "\n<style id=\"puter-injected-css\">\n$cssContent\n</style>\n"
        val scriptTag = "\n<script id=\"puter-injected-js\">\n$jsContent\n</script>\n"

        // Inject inside <head> and before </body>
        cleanedHtml = if (cleanedHtml.contains("</head>", ignoreCase = true)) {
            cleanedHtml.replaceFirst("</head>", "$shimScript$styleTag</head>", ignoreCase = true)
        } else {
            "$shimScript$styleTag$cleanedHtml"
        }

        cleanedHtml = if (cleanedHtml.contains("</body>", ignoreCase = true)) {
            cleanedHtml.replaceFirst("</body>", "$scriptTag</body>", ignoreCase = true)
        } else {
            "$cleanedHtml$scriptTag"
        }

        return cleanedHtml
    }
}
