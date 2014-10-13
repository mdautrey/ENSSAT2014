function Hello($scope, $http) {
    $http.get('http://localhost:8080/individu').
        success(function(data) {
            $scope.individu = data;
        });
}
