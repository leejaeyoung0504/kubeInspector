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

                    <!-- Page Heading -->
                    <h1 class="h3 mb-2 text-gray-800">Tables</h1>
                    <p class="mb-4">DataTables is a third party plugin that is used to generate the demo table below.
                        For more information about DataTables, please visit the <a target="_blank"
                            href="https://datatables.net">official DataTables documentation</a>.</p>

                    <!-- DataTales Example -->
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">DataTables Example</h6>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                    <thead>
                                        <tr>
                                            <th>Host Name</th>
                                            <th>Progress</th>
                                            <th>Status</th>
                                            <th>Message</th>
                                        </tr>
                                    </thead>
                                    <tbody>
	                                    <c:forEach var="entry" items="${hashMap}">
							                <tr>
							                    <td>${entry.key}</td> <!-- 서버 호스트네임 -->
							                    <td>
							                        <div class="progress">
							                            <div id="progress-bar-${entry.value}" class="progress-bar" role="progressbar" style="width: 0%"
							                                aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
							                        </div>
							                    </td>
							                    <td id="status-${entry.value}">Pending</td> <!-- 초기 상태는 'Pending' -->
							                    <td>
												    <a id="info-button-${entry.value}" href="#" class="btn btn-info btn-circle" data-toggle="modal" data-target="#info-modal-${entry.value}">
												        <i class="fas fa-info-circle"></i>
												    </a>
												</td>
							                </tr>
							            </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <form id="installForm" action="/install/confiemHostNext" method="POST" class="form-container">
                        	<input type="hidden" name="stepId" value="${stepId}">
	                        <div class="btn-container">
			                	<button type="button" class="btn btn-success btn-icon-split" onclick="submitForm('back')"> 
						        	<span class="text">Back</span>
								</button>
			                    <button type="submit"  form="installForm" class="btn btn-success btn-icon-split"> 
						        	<span class="text">Next</span>
								</button>
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

		<!-- Scroll to Top Button-->
		<a class="scroll-to-top rounded" href="#page-top"> <i
			class="fas fa-angle-up"></i>
		</a>

		<!-- Info Modal-->
		<!-- Info Modal-->
		<c:forEach var="entrys" items="${hashMap}">
		    <div class="modal fade" id="info-modal-${entrys.value}" tabindex="-1" role="dialog" aria-labelledby="info-modal-label-${entrys.value}" aria-hidden="true">
		        <div class="modal-dialog" role="document">
		            <div class="modal-content">
		                <div class="modal-header">
		                    <h5 class="modal-title" id="info-modal-label-${entrys.value}">Log Info</h5>
		                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
		                        <span aria-hidden="true">&times;</span>
		                    </button>
		                </div>
		                <div id="modal-text-${entrys.value}" class="modal-body">
		                    <!-- 로그 정보가 여기에 표시됩니다 -->
		                </div>
		                <div class="modal-footer">
		                    <button class="btn btn-secondary" type="button" data-dismiss="modal">Close</button>
		                </div>
		            </div>
		        </div>
		    </div>
		</c:forEach>
		

		<!-- Bootstrap core JavaScript-->
		<script src="../static/vendor/jquery/jquery.min.js"></script>
		<script src="../static/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

		<!-- Core plugin JavaScript-->
		<script src="../static/vendor/jquery-easing/jquery.easing.min.js"></script>

		<!-- Custom scripts for all pages-->
		<script src="../static/js/sb-admin-2.min.js"></script>

		<!-- Page level plugins -->
		<!-- <script src="../static/vendor/chart.js/Chart.min.js"></script> -->

		<!-- Page level custom scripts -->
		<!-- <script src="../static/js/demo/chart-area-demo.js"></script>
		<script src="../static/js/demo/chart-pie-demo.js"></script> -->
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
            input.value = 'confiemHosts';
            input.id = 'actionType';  // 필드 ID 추가
            form.appendChild(input);
            
            form.action = '/install/installOptionsBack';  // Back 버튼 클릭 시 이동할 경로 
        } else if (actionType === 'next') {
            form.action = '/install/confiemHostNext';  // Next 버튼 클릭 시 이동할 경로
        }
        
        // 폼 전송
        form.submit();
    }
    
 // AJAX로 진행 상태와 로그를 주기적으로 확인하는 함수
    function checkProgress(serverId) {
            $.ajax({
            	type:'post',
                url: '/install/checkInstallProgress', // 설치 진행 상태를 확인하는 API
                data: { serverId: serverId },
                success: function(data) {
                }
            });
        }
    function getProgress() {
        $.ajax({
            type: 'get',
            url: '/install/getInstallProgress',
            success: function(response) {
            	console.log("jytest In success");
                // null 값이 넘어올 경우 빈 배열로 처리
                var progressList = response.progressList || [];
                var logsList = response.logsList || [];
                var allCompleted = true; // 모든 서버가 100% 완료되었는지 확인하기 위한 플래그
                var resultPercent = 0;

                if(logsList.length == 0){
                	allCompleted = false;
                }
                // progressList가 존재할 경우에만 처리
                if (progressList.length > 0) {
                    for (var n in progressList) {
                        console.log(progressList[n]);
                        $('#progress-bar-' + progressList[n].server.id).css('width', progressList[n].progressPercent + '%');
                        $('#progress-bar-' + progressList[n].server.id).attr('aria-valuenow', progressList[n].progressPercent);
                        $('#status-' + progressList[n].server.id).text(progressList[n].status);
                        resultPercent = progressList[n].progressPercent;
                        // 진행 상태가 100%가 아닌 서버가 있으면 플래그를 false로 설정
                        if (progressList[n].progressPercent < 100) {
                            allCompleted = false;
                        }
                    }
                }

                // logsList가 존재할 경우에만 처리
                if (logsList.length > 0 && resultPercent == 100) {
                    for (var n in logsList) {
                        $('#modal-text-' + logsList[n].server.id).append(logsList[n].logMessage + '<br>');
                    }
                }

                // 모든 서버가 완료되지 않았다면 5초 후 다시 getProgress 호출
                if (!allCompleted) {
                    setTimeout(getProgress, 1000); // 5초마다 진행 상태를 확인
                }
            },
            error: function() {
                console.log("Error fetching progress");
            }
        });
    }

        // 문서가 로드되면 모든 서버에 대해 진행 상태를 확인 시작
        $(document).ready(function() {
        	// 서버 ID 리스트를 배열로 만듦
            var serverIds = [];
            <c:forEach var="entry" items="${hashMap}">
                serverIds.push('${entry.value}');
            </c:forEach>
            // 서버 ID 리스트를 한 번만 보냄
            checkProgress(serverIds);
            getProgress();
        });
</script>