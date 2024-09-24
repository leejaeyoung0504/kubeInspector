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
<link href="../static/css/sb-admin-2.min.css" rel="stylesheet">

</head>

<body id="page-top">

	<!-- Page Wrapper -->
	<div id="wrapper">

		<!-- Sidebar -->
		<%@ include file="/WEB-INF/views/common/sidebar-install.jsp"%>
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
					<h1 class="h3 mb-0 text-gray-800">Get Started</h1>
					<p class="mb-4">쿠버네티스 설치 과정을 단계별로 안내해 드리겠습니다. 클러스터의 이름을 설정
						해주세요.</p>

					<div class="card shadow mb-4">
						<div class="card-header py-3">
							<h6 class="m-0 font-weight-bold text-primary">클러스터명 입력</h6>
						</div>
						<div class="card-body">
							<div class="col-lg-6">
								<div class="card mb-4 py-3 border-left-primary">
									<form
										class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search"
										action="/install/getStarted" method="POST">
										<div class="input-group">
											<!-- Cluster Name 입력 필드 -->
											<input type="text" name="clusterName"
												class="form-control bg-light border-0 small"
												placeholder="Cluster Name" aria-describedby="basic-addon2"
												required value="${clustarName}">

											<!-- Step ID를 히든 필드로 추가 (서버에서 가져온 값을 삽입) -->
											<input type="hidden" name="stepId" value="${stepId}">

											<!-- Submit 버튼 -->
											<div class="input-group-append">
												<button type="submit" class="btn btn-success">
													<span class="text">Next</span>
												</button>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>


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