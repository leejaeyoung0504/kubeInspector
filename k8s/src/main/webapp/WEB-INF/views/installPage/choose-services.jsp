<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
					<h1 class="h3 mb-0 text-gray-800">Choose Services</h1>
					<p class="mb-4">쿠버네티스 설치 노드별 master, infra, worker 선택</p>
					
					
					<form id="installForm" action="/install/chooseServiceNext" method="POST" class="form-container">
						<div class="row">
							<c:forEach var="server" items="${servers}">
								<div class="col-xl-3 col-md-6 mb-4">
		                            <div class="card border-left-primary shadow h-100 py-2">
		                                <div class="card-body">
		                                    <div class="row no-gutters align-items-center">
		                                        <div class="col mr-2">
		                                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">${server.hostname }</div>
		                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
		                                            	<select class="cs-1" name="${server.id }">
														  <option selected>Select Node Type</option>
														  <c:forEach var="role" items="${roleList }">
														  	<option value="${role.id }">${role.roleName }</option>
														  </c:forEach>
														</select>
		                                            </div>
		                                        </div>
		                                        <div class="col-auto">
		                                            <i class="fab fa-redhat fa-2x text-gray-300"></i>
		                                        </div>
		                                    </div>
		                                </div>
		                            </div>
		                        </div>
							</c:forEach>
							<input type="hidden" name="stepId" value="${stepId}">
							<div class="btn-container">
			                	<button type="button" class="btn btn-success btn-icon-split" onclick="submitForm('back')">
			                        <span class="text">Back</span>
			                    </button>
			                    <button type="submit"  form="installForm" class="btn btn-success btn-icon-split"> 
						        	<span class="text">Next</span>
								</button>
							</div>
	                      </div>
					</form>
                        
				</div>
				<!-- End of Main Content -->

				<!-- Footer -->
				<%@ include file="/WEB-INF/views/samples/common/footer.jsp"%>
				<!-- End of Footer -->

			</div>
			<!-- End of Content Wrapper -->

		</div>
		<!-- End of Page Wrapper -->

		<!-- Scroll to Top Button-->
		<a class="scroll-to-top rounded" href="#page-top"> <i
			class="fas fa-angle-up"></i>
		</a>


		<!-- Bootstrap core JavaScript-->
		<script src="../static/vendor/jquery/jquery.min.js"></script>
		<script src="../static/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

		<!-- Core plugin JavaScript-->
		<script src="../static/vendor/jquery-easing/jquery.easing.min.js"></script>

		<!-- Custom scripts for all pages-->
		<script src="../static/js/sb-admin-2.min.js"></script>

		<!-- Page level plugins -->
		<script src="../static/vendor/chart.js/Chart.min.js"></script>

		<!-- Page level custom scripts -->
		<script src="../static/js/demo/chart-area-demo.js"></script>
		<script src="../static/js/demo/chart-pie-demo.js"></script>
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
        input.value = 'chooseService';
        input.id = 'actionType';  // 필드 ID 추가
        form.appendChild(input);
        
        form.action = '/install/installOptionsBack';  // Back 버튼 클릭 시 이동할 경로 
    } else if (actionType === 'next') {
        form.action = '/install/chooseServiceNext';  // Next 버튼 클릭 시 이동할 경로
    }
    
    // 폼 전송
    form.submit();
}


$(document).ready(function() {
	
});
</script>