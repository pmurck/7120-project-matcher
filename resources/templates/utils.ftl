<#macro nav>
<nav class="navbar navbar-expand navbar-dark bg-dark mb-4">
    <a class="navbar-brand" href="/">Project Matcher</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
                <a class="nav-link" href="/">Home</a>
            </li>
        </ul>
        <form class="form-inline mt-2 mt-md-0" action="/logout" method="post">
            <button class="btn btn-outline-light my-2 my-sm-0" type="submit">Cerrar sesión</button>
        </form>
    </div>
</nav>
</#macro>