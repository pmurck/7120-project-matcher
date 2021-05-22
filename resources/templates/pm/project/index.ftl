<#-- @ftlvariable name="project" type="com.pmurck.projectMatcher.model.Project" -->
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
        <h1>Proyecto: ${project.name}</h1>
        <p>Están disponibles las siguientes acciones:</p>
        <a class="btn btn-secondary btn-lg btn-block" href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/edit" role="button">Editá los datos del Proyecto</a>
        <hr/>
        <a class="btn btn-info btn-lg btn-block" href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/priorities" role="button">Priorizá a los Desarrolladores</a>
        <hr/>
        <a class="btn btn-primary btn-lg btn-block" href="/org/${project.pm.org.code}/pm/project/${project.idInPm}/assignments" role="button">Ver asignaciones</a>
    </div>
</main>

<!--
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script>window.jQuery || document.write('<script src="../assets/js/vendor/jquery.slim.min.js"><\/script>')</script><script src="../assets/dist/js/bootstrap.bundle.js"></script></body>
-->
</html>

