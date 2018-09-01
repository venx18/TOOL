// CONTROLLER UPLOAD FILE
jsaApp.controller('uploadFileController', ['$scope', '$http', function($scope, $http){
    $scope.doUploadFile = function(){
       var file = $scope.uploadedFile;
       var url = "/api/convertXmlToJson";
       
       var data = new FormData();
       data.append('uploadfile', file);
      // data.append('convertXmlToJson', file);
    
       var config = {
    	   	transformRequest: angular.identity,
    	   	transformResponse: angular.identity,
	   		headers : {
	   			'Content-Type': undefined
	   	    }
       }
       
       $http.post(url, data, config).then(function (response) {
			$scope.uploadResult=response.data;
		}, function (response) {
			$scope.uploadResult=response.data;
		});
    };
}]);
