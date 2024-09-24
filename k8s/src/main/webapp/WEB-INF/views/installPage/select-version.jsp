<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">

<title>SB Admin 2 - Dashboard</title>

<!-- Custom fonts for this template-->
<link href="../static/vendor/fontawesome-free/css/all.css"
	rel="stylesheet" type="text/css">
<link
	href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
	rel="stylesheet">

<!-- Custom styles for this template-->
<link href="../static/css/sb-admin-2.css" rel="stylesheet">

</head>

<body id="page-top">

	<!-- Page Wrapper -->
	<div id="wrapper">

		<!-- Sidebar -->
		<%@ include file="/WEB-INF/views/samples/common/sidebar-install.jsp"%>
		<!-- End of Sidebar -->

		<!-- Content Wrapper -->
		<div id="content-wrapper" class="d-flex flex-column">

			<!-- Main Content -->
			<div id="content">

		    <!-- Topbar -->
		    <%@ include file="/WEB-INF/views/samples/common/nav.jsp"%>
		    <!-- End of Topbar -->
		
		    <!-- Begin Page Content -->
		    <div class="container-fluid">
		        <h1 class="h3 mb-0 text-gray-800">Select Version</h1>
		        <p class="mb-4">쿠버네티스 설치 버전 선택</p>
		
		        <form id="installForm" action="/install/selectVersionNext" method="POST" class="form-container">
		            <div class="row">
		                <c:forEach var="file" items="${installFile}">
		                    <!-- Skip file types with ID 8, 9, 10, 11 -->
		                    <c:if test="${file.fileType.id != 8 && file.fileType.id != 9 && file.fileType.id != 10 && file.fileType.id != 11}">
		
		                        <!-- Set card border class based on fileType.id -->
		                        <c:set var="borderClass">
		                            <c:choose>
		                                <c:when test="${file.fileType.id == 1}">border-left-primary</c:when>
		                                <c:when test="${file.fileType.id == 2}">border-left-success</c:when>
		                                <c:when test="${file.fileType.id == 3}">border-left-info</c:when>
		                                <c:when test="${file.fileType.id == 4}">border-left-warning</c:when>
		                                <c:when test="${file.fileType.id == 5}">border-left-primary</c:when>
		                                <c:when test="${file.fileType.id == 6}">border-left-success</c:when>
		                                <c:otherwise>border-left-success</c:otherwise>
		                            </c:choose>
		                        </c:set>
		
		                        <!-- Card -->
		                        <div class="col-xl-3 col-md-6 mb-4">
		                            <div class="card ${borderClass} shadow h-100 py-2">
		                                <div class="card-body">
		                                    <div class="row no-gutters align-items-center">
		                                        <div class="col mr-2">
		                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">${file.fileType.fileType}</div>
		                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
		                                                <select class="cs-1" data-filetype="${file.fileType.fileType}" name="${file.fileType.fileType }">
		                                                    <option selected>Choose ${file.fileType.fileType} file</option>
		                                                </select>
		                                            </div>
		                                        </div>
		                                        <div class="col-auto">
		                                            <!-- Set icon based on fileType.fileType -->
		                                            <c:set var="iconClass">
		                                                <c:choose>
		                                                    <c:when test="${file.fileType.fileType == 'RUNC'}">fab fa-ravelry</c:when>
		                                                    <c:when test="${file.fileType.fileType == 'CRI'}">fab fa-contao</c:when>
		                                                    <c:when test="${file.fileType.fileType == 'CONTAINERD'}">fas fa-boxes</c:when>
		                                                    <c:when test="${file.fileType.fileType == 'CNI'}">fas fa-dice-d6</c:when>
		                                                    <c:otherwise>fas fa-dharmachakra</c:otherwise>
		                                                </c:choose>
		                                            </c:set>
		                                            <i class="${iconClass} fa-2x text-gray-300"></i>
		                                        </div>
		                                    </div>
		                                </div>
		                            </div>
		                        </div>
		
		                    </c:if>
		                </c:forEach>
		                <div class="col-xl-3 col-md-6 mb-4">
                            <div class="card border-left-secondary shadow h-100 py-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">CRI</div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            	<select class="cs-1" data-filetype="CRI" name="CRI">
		                                        	<option selected>Choose CRI file</option>
		                                        	<option value="cailco">Cailco</option>
		                                        </select>
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fab fa-contao fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
		
		                <!-- Hidden field and form buttons -->
		                <input type="hidden" name="stepId" value="${stepId}">
		                <div class="btn-container">
		                    <button type="button" class="btn btn-success btn-icon-split" onclick="submitForm('back')">
		                        <span class="text">Back</span>
		                    </button>
		                    <button type="submit" form="installForm" class="btn btn-success btn-icon-split">
		                        <span class="text">Next</span>
		                    </button>
		                </div>
		
		            </div>
		        </form>
		    </div>
		</div>
				<!-- End of Main Content -->

				<!-- Footer -->
				<%@ include file="/WEB-INF/views/samples/common/footer.jsp"%>
				<!-- End of Footer -->

			</div>
			<!-- End of Content Wrapper -->

		</div>
		<!-- End of Page Wrapper -->

		<!-- Bootstrap core JavaScript-->
		<script src="../static/vendor/jquery/jquery.min.js"></script>
		<script src="../static/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

		<!-- Core plugin JavaScript-->
		<script src="../static/vendor/jquery-easing/jquery.easing.min.js"></script>

		<!-- Custom scripts for all pages-->
		<script src="../static/js/sb-admin-2.min.js"></script>

</body>

</html>
<script>
function submitForm(actionType) {
    var form = document.getElementById('installForm');
    
    // 버튼에 따라 다른 action 설정
    if (actionType === 'back') {
    	var input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'actionType';
        input.value = 'selectVersion';
        input.id = 'actionType';  // 필드 ID 추가
        form.appendChild(input);
        
        form.action = '/install/installOptionsBack';  // Back 버튼 클릭 시 이동할 경로 
    } else if (actionType === 'next') {
        form.action = '/install/selectVersionNext';  // Next 버튼 클릭 시 이동할 경로
    }
    
    // 폼 전송
    form.submit();
}


$(document).ready(function() {
	// 서버에서 전달된 JSON 배열을 파싱
    var installFileList = JSON.parse('${installFileList}'); // JSON 데이터를 파싱
    // 각 파일 타입에 맞는 select box에 옵션 추가
    installFileList.forEach(function(file) {
    	
        var fileType = file.fileType;

        // select 요소 선택 (fileType에 맞는 곳에 넣기)
        var selectElement = $("select[data-filetype='" + fileType + "']");
        
        // 해당 select에 파일명 옵션 추가
        selectElement.append('<option value="' + file.filePath + '">' + file.fileName + '</option>');
    });
});
</script>