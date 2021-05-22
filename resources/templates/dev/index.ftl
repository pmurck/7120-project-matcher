<#-- @ftlvariable name="dev" type="com.pmurck.projectMatcher.model.Developer" -->
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
        <h1>Desarrollador en ${dev.org.name}</h1>
        <p>Están disponibles las siguientes acciones:</p>
        <a class="btn btn-secondary btn-lg btn-block" href="/org/${dev.org.code}/dev/edit" role="button">Editá tus datos</a>
        <hr/>
        <a class="btn btn-info btn-lg btn-block" href="/org/${dev.org.code}/dev/priorities" role="button">Priorizá a los Proyectos</a>
        <hr/>
        <a class="btn btn-primary btn-lg btn-block" href="/org/${dev.org.code}/dev/assignments" role="button">Ver asignaciones</a>
    </div>
</main>
</html>

