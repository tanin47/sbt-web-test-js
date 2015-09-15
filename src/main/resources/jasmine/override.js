var consoleOutputs = [];
var summary = {
  total: 0,
  success: 0,
  failure: 0
};

console._log = console.log

console.log = function() {
  var line = "";
  for (var i=0;i<arguments.length;i++) {
    line += arguments[i] + " ";
  }
  consoleOutputs.push(line);
};

var reporter = {
  jasmineStarted: function(suiteInfo) {
  },
  suiteStarted: function(result) {
    console.log(result.fullName);
  },
  specStarted: function(result) {
  },
  specDone: function(result) {
    console.log(' - ' + result.fullName + ' [' + result.status + ']');
    for(var i = 0; i < result.failedExpectations.length; i++) {
      console.log('   Failure: ' + result.failedExpectations[i].message);
      console.log('            ' + result.failedExpectations[i].stack);
    }

    summary.total++;

    if (result.status == 'passed') {
      summary.success++;
    } else {
      summary.failure++;
    }
  },
  suiteDone: function(result) {},
  jasmineDone: function() {}
};