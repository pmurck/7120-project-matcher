<#-- @ftlvariable name="data" type="com.pmurck.projectMatcher.IndexData" -->
<#-- @ftlvariable name="errors" type="java.util.Map<String,String>" -->
<#import "utils.ftl" as utils>
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
        <h1>Hola, ${data.user.firstName}</h1>
        <#if data.roles?has_content>
        <p class="lead">Acá podes entrar a tus organizaciones asociadas</p>
        <div class="list-group">
            <#list data.roles as role>
            <a href="${role.first.hrefToIndex(role.second.code)}" class="list-group-item list-group-item-action">
                ${role.first.desc} @ ${role.second.name}
            </a>
            </#list>
        </div>
        </#if>
        <hr/>
        <h3>Unirme a una organización existente</h3>
        <form action="/org/join" method="post">
            <div class="form-group <#if errors?has_content>was-validated</#if>">
                <label for="organizationCode">Código de la organización</label>
                <input type="text" class="form-control" id="organizationCode" name="organizationCode" minlength="4" maxlength="4" placeholder="WXYZ" style="text-transform:uppercase" required>
                <#if errors.invalid_orgcode?has_content>
                    <div class="invalid-feedback">${errors.invalid_orgcode}</div>
                </#if>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="joinType" id="devOption" value="dev" required>
                <label class="form-check-label" for="devOption">Desarrollador</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="joinType" id="pmOption" value="pm" required>
                <label class="form-check-label" for="pmOption">Admin. de Proyectos</label>
            </div>
            <button type="submit" class="btn btn-primary">Unirme</button>
        </form>
        <hr/>
        <h3>Crear organización</h3>
        <form action="/org" method="post">
            <div class="form-group">
                <label for="organizationName">Nombre de la organización</label>
                <input type="text" class="form-control" id="organizationName" name="organizationName" placeholder="Nombre" required>
            </div>
            <button type="submit" class="btn btn-primary">Crear</button>
        </form>
    </div>

</main>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script>window.jQuery || document.write('<script src="../assets/js/vendor/jquery.slim.min.js"><\/script>')</script>

</body>
</html>

