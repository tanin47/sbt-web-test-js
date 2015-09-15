describe("Test from Coffeescript", () ->
  loadJS = (file) ->
    js = $.ajax({ type: "GET", url: file, async: false }).responseText

  currentScriptPath = () ->
    scripts = document.querySelectorAll( 'script[src]' );
    currentScript = scripts[ scripts.length - 1 ].src;
    currentScriptChunks = currentScript.split( '/' );
    currentScriptFile = currentScriptChunks[ currentScriptChunks.length - 1 ];

    currentScript.replace(currentScriptFile, '' );

  console.log(currentScriptPath())
  console._log(currentScriptPath())

  loadJS(currentScriptPath() + '/../../lib/lib.js')
  it("check the value from lib.coffee", () ->
    expect(window.someValueFromLibCoffee).toBe('yes')
  )
)