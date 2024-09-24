<!-- Sidebar -->
<ul
	class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion"
	id="accordionSidebar">

	<!-- Sidebar - Brand -->
	<a class="sidebar-brand d-flex align-items-center justify-content-center" href="/samples/" >
		<div class="sidebar-brand-icon rotate-n-15">
			<i class="fas fa-laugh-wink"></i>
		</div>
		<div class="sidebar-brand-text mx-3">Install</div>
	</a>

	<!-- Divider -->
	<hr class="sidebar-divider my-0">

	<!-- Nav Item - Dashboard -->
	<li class="nav-item <c:if test="${stepId == 1 }">active</c:if> ">
		<a class="nav-link" href="/samples/"> 
			<c:choose>
				<c:when test="${stepId == 1 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Get Started</span>
		</a>
	</li>

	<li class="nav-item <c:if test="${stepId == 2 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 2 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Install Options</span>
		</a>
	</li>
	
	<li class="nav-item <c:if test="${stepId == 3 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 3 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Confirm Hosts</span>
		</a>
	</li>
	
	<li class="nav-item <c:if test="${stepId == 4 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 4 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Select Version</span>
		</a>
	</li>
	
	<li class="nav-item <c:if test="${stepId == 5 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 5 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Choose Services</span>
		</a>
	</li>
	
	<li class="nav-item <c:if test="${stepId == 6 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 6 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Install, Start and Test</span>
		</a>
	</li>

	<li class="nav-item <c:if test="${stepId == 7 }">active</c:if> ">
		<a class="nav-link" href="/samples/charts"> 
			<c:choose>
				<c:when test="${stepId == 7 }">
					<i class="fas fa-check"></i>
				</c:when>
				<c:otherwise>
					<i class="fas fa-info-circle"></i>
				</c:otherwise>
			</c:choose> 
			<span>Summary</span>
		</a>
	</li>


	<!-- Divider -->
	<hr class="sidebar-divider">

</ul>
<!-- End of Sidebar -->