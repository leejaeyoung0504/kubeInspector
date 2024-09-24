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
<link href="../static/vendor/fontawesome-free/css/all.min.css"
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
					<h1 class="h3 mb-0 text-gray-800">Install Options</h1>
					<p class="mb-4">쿠버네티스를 설치할 노드의 정보 입력.</p>

					<form id="installForm" action="/install/host" method="POST" class="form-container">
						<input type="hidden" name="stepId" value="${stepId}">
			            <div class="form-group">
				            <div class="row">
					            <div class="col-lg-6">
					            	<div class="card shadow mb-4">
					                	<div class="card-header py-3">
					                		<h6 class="m-0 font-weight-bold text-primary">Host Name</h6>
					                     </div>
					                     <div class="card-body">
					                     	<p>Host 정보 입력</p>
					                        <textarea class="te-1" id="hostnames" name="hostnames" placeholder="example.co.kr&#10;example.co.kr&#10;example.co.kr" required><c:forEach var="entry" items="${hostInfo}">${entry.key}:${entry.value}&#10;</c:forEach></textarea>
					                     </div>
					                </div>
					                
					                <div class="card shadow mb-4">
					                	<div class="card-header py-3">
					                		<h6 class="m-0 font-weight-bold text-primary">SSH Key</h6>
					                     </div>
					                     <div class="card-body">
					                     	<p>SSH KEY 입력</p>
					                        <textarea class="te-1" id="sshKey" name="sshKey" placeholder="Enter SSH Key" required>${sshKey}</textarea>
					                     </div>
					                </div>
					        	</div>
					        	<div class="col-lg-6">
					            	<div class="card shadow mb-4">
					                	<div class="card-header py-3">
					                		<h6 class="m-0 font-weight-bold text-primary">Port</h6>
					                     </div>
					                     <div class="card-body">
					                     	<p>Port 입력</p>
					                        <input class="in-1" type="text" id="sshPort" name="sshPort" placeholder="Port" required value="${port }">
					                     </div>
					                </div>
					                
					                <div class="card shadow mb-4">
					                	<div class="card-header py-3">
					                		<h6 class="m-0 font-weight-bold text-primary">User</h6>
					                     </div>
					                     <div class="card-body">
					                     	<p>User 입력</p>
					                        <input class="in-1" type="text" id="sshUser" name="sshUser" value="root" readonly>
					                     </div>
					                </div>
					                
					                <div class="card shadow mb-4">
					                	<div class="card-header py-3">
					                		<h6 class="m-0 font-weight-bold text-primary">NAS DIR</h6>
					                     </div>
					                     <div class="card-body">
					                     	<p>NAS DIR 입력</p>
					                        <input class="in-1" type="text" id="nasDirectory" name="nasDirectory" placeholder="/upload/" required value="${nfsDir }">
					                     </div>
					                </div>
					                 <div class="card shadow mb-4">
					                    <div class="btn-container">
					                        <!-- Back 버튼 -->
					                        <button type="button" class="btn btn-success btn-icon-split" onclick="submitForm('back')"> 
					                            <span class="text">Back</span>
					                        </button>
					
					                        <!-- Next 버튼 -->
					                        <button type="button" class="btn btn-success btn-icon-split" onclick="submitForm('next')"> 
					                            <span class="text">Next</span>
					                        </button>
					                    </div>
					                </div>
					        	</div>
				            </div>
			            </div>
			        </form>

        


					<!-- /.container-fluid -->

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

		<!-- Logout Modal-->
		<div class="modal fade" id="logoutModal" tabindex="-1" role="dialog"
			aria-labelledby="exampleModalLabel" aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel">Ready to
							Leave?</h5>
						<button class="close" type="button" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">Ã</span>
						</button>
					</div>
					<div class="modal-body">Select "Logout" below if you are
						ready to end your current session.</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button"
							data-dismiss="modal">Cancel</button>
						<a class="btn btn-primary" href="login.html">Logout</a>
					</div>
				</div>
			</div>
		</div>

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
            input.value = 'installOptrions';
            input.id = 'actionType';  // 필드 ID 추가
            form.appendChild(input);
            
            form.action = '/install/installOptionsBack';  // Back 버튼 클릭 시 이동할 경로 
        } else if (actionType === 'next') {
            form.action = '/install/installOptionsNext';  // Next 버튼 클릭 시 이동할 경로
        }
        
        // 폼 전송
        form.submit();
    }
</script>