<#import "manager/parts/common.ftl" as c>
<#import "manager/parts/login.ftl" as l>

<@c.page>
Add new user
${message}
<@l.login "/registration" />
</@c.page>
