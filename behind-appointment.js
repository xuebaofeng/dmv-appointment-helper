var request = require('request')
var jsdom = require("jsdom");

//change this to your intetested locations
var data = [644, 579, 604]

for (var i = 0; i < data.length; i++) {
    request.post({
            url: 'https://www.dmv.ca.gov/wasapp/foa/findDriveTest.do',
			//change this to you personal info
            form: {
                birthDay: '08', birthMonth: '08', birthYear: '1988',
                dlNumber: '88888', firstName: 'xxx', lastName: 'yyy',
                numberItems: '1', officeId: data[i], requestedTask: 'DT', resetCheckFields: 'true',
                telArea: '111', telPrefix: '222', telSuffix: '3333'
            }
        },
        function (err, httpResponse, body) {

            jsdom.env(
                body,
                ["http://code.jquery.com/jquery.js"],
                function (err, window) {
                    var address = window.$("#app_content table tbody tr td address").text();
                    address = address.split('\n')[0];
                    console.log(address)
                    var time = window.$("#app_content table tbody tr td p.alert").text();
                    time = time.split(':')[1]
                    console.log(time);
                    window.close();
                }
            );

        }
    )
}

