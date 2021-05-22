<#-- @ftlvariable name="org" type="com.pmurck.projectMatcher.model.Organization" -->
<#import "/utils.ftl" as utils>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <title>Project Matcher</title>
    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link href="https://unpkg.com/tabulator-tables@4.7.2/dist/css/tabulator.min.css" rel="stylesheet">
    <script type="text/javascript" src="https://unpkg.com/tabulator-tables@4.7.2/dist/js/tabulator.min.js"></script>
</head>
<body>

<@utils.nav></@utils.nav>

<main role="main" class="container">
    <div class="jumbotron">
        <h1>${org.name}</h1>
        <a class="btn btn-primary" href="./${org.code}/solve" role="button" style="float: right;">Ver asignaciones</a>
        <p class="lead">Detalle de los usuarios asociados a tu organización</p>
        <p class="lead">Si queres que se unan a tu organización compartiles el código: <b>${org.code}</b></p>
        <h3>Desarrolladores</h3>
        <table id="devs" class="table table-light table-striped table-hover table-sm">
            <thead>
            <tr>
                <th scope="col">#</th>
                <th scope="col">Desarrollador</th>
                <th scope="col">Seniority</th>
                <th scope="col">Disponibilidad (hs)</th>
            </tr>
            </thead>
            <tbody>
            <#assign devCount = 1>
            <#list org.devs?filter(d -> d.active) as dev>
                <tr>
                    <th scope="row">${devCount}</th> <#assign devCount++>
                    <td>${dev.user.firstName} ${dev.user.lastName}</td>
                    <td>${dev.seniority.desc}</td>
                    <td>${dev.availabilityHours}</td>
                    <td><a href="/org/${dev.org.code}/dev/edit?devId=${dev.idInOrg}" class="fa fa-pencil-square-o" aria-hidden="true" style="color: inherit;"></a></td>
                    <td><a href="/org/${dev.org.code}/dev/switchState?devId=${dev.idInOrg}" class="fa fa-times" aria-hidden="true" style="color: inherit;"></a></td>
                </tr>
            </#list>
            </tbody>
            <#list org.devs?filter(d -> !d.active) as dev>
            <#if dev?is_first>
                <tbody>
                <tr><th colspan="10" style="
                    background-color: darkgrey;
                    text-align: center;
                    border-color: darkgray;
                ">Inhabilidatos</th>
                </tr>
            </#if>
                <tr>
                    <th scope="row">${devCount}</th> <#assign devCount++>
                    <td>${dev.user.firstName} ${dev.user.lastName}</td>
                    <td>${dev.seniority.desc}</td>
                    <td>${dev.availabilityHours}</td>
                    <td><a href="/org/${dev.org.code}/dev/edit?devId=${dev.idInOrg}" class="fa fa-pencil-square-o" aria-hidden="true" style="color: inherit;"></a></td>
                    <td><a href="/org/${dev.org.code}/dev/switchState?devId=${dev.idInOrg}" class="fa fa-check" aria-hidden="true" style="color: inherit;"></a></td>
                </tr>
            <#if dev?is_last>
                </tbody>
            </#if>
            </#list>
        </table>
        <h3>Proyectos</h3>
        <table id="projects" class="table table-light table-striped table-hover table-sm">
            <thead>
            <tr>
                <th scope="col">#</th>
                <th scope="col">Proyecto</th>
                <th scope="col">Administrador (usuario)</th>
                <th scope="col">Total requerido (hs)</th>
            </tr>
            </thead>
            <tbody>
            <#assign projectCount = 1>
            <#assign hasInactiveProjects = false>
            <#list org.pms as pm>
                <#list pm.projects as project>
                    <#if project.active>
                    <tr>
                        <th scope="row">${projectCount}</th> <#assign projectCount++>
                        <td>${project.name}</td>
                        <td>${pm.user.firstName} ${pm.user.lastName} (${pm.user.name})</td>
                        <td>${project.getTotalRequiredHours()}</td>
                        <td><a href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/edit?pmId=${pm.idInOrg}" class="fa fa-pencil-square-o" aria-hidden="true" style="color: inherit;"></a></td>
                        <td><a href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/switchState?pmId=${pm.idInOrg}" class="fa fa-times" aria-hidden="true" style="color: inherit;"></a></td>
                    </tr>
                        <#else><#assign hasInactiveProjects = true>
                    </#if>
                </#list>
            </#list>
            </tbody>
            <#if hasInactiveProjects>
            <tbody>
            <tr><th colspan="10" style="
                    background-color: darkgrey;
                    text-align: center;
                    border-color: darkgray;
                ">Inhabilitados</th>
            </tr>
            <#list org.pms as pm>
                <#list pm.projects?filter(p -> !p.active) as project>
                        <tr>
                            <th scope="row">${projectCount}</th> <#assign projectCount++>
                            <td>${project.name}</td>
                            <td>${pm.user.firstName} ${pm.user.lastName} (${pm.user.name})</td>
                            <td>${project.getTotalRequiredHours()}</td>
                            <td><a href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/edit?pmId=${pm.idInOrg}" class="fa fa-pencil-square-o" aria-hidden="true" style="color: inherit;"></a></td>
                            <td><a href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/switchState?pmId=${pm.idInOrg}" class="fa fa-check" aria-hidden="true" style="color: inherit;"></a></td>
                        </tr>
                </#list>
            </#list>
            </tbody>
            </#if>
        </table>
    </div>
</main>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script>window.jQuery || document.write('<script src="../assets/js/vendor/jquery.slim.min.js"><\/script>')</script>

</body>
<script>
    /*var table = new Tabulator("#projects", {
        layout:"fitColumns",
        responsiveLayout:"hide"
    });*/
</script>
</html>

