<#-- @ftlvariable name="pm" type="com.pmurck.projectMatcher.model.ProjectManager" -->
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
</head>
<body>

<@utils.nav></@utils.nav>

<main role="main" class="container">
    <div class="jumbotron">
        <h1>Admin. de Proyectos en ${pm.org.name}</h1>
        <p class="lead">Acá esta tu listado de proyectos</p>
        <div class="list-group">
            <#list pm.projects as project>
            <a href="/org/${pm.org.code}/pm/project/${project.idInPm}" class="list-group-item list-group-item-action">
                ${project.name} con ${project.getTotalRequiredHours()} horas totales requeridas
            </a>
            </#list>
        </div>
        <hr/>
        <a class="btn btn-primary btn-lg btn-block" href="/org/${pm.org.code}/pm/project/new" role="button">Crear nuevo proyecto</a>
    </div>
</main>

<!--
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script>window.jQuery || document.write('<script src="../assets/js/vendor/jquery.slim.min.js"><\/script>')</script><script src="../assets/dist/js/bootstrap.bundle.js"></script></body>
-->
</html>

