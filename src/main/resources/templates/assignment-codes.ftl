<#-- @ftlvariable name="content" type="org.empowrco.coppin.assignment.presenters.GetAssignmentPortalResponse" -->
<script>
    $(document).ready(function () {
        const table = $('#assignment-codes-table').DataTable({
            language: {
                search: "",
                emptyTable: "If no code is given, students will be allowed to complete in any language."
            },
            columnDefs: [
                {
                    target: 5,
                    visible: false,
                },
                {
                    target: 6,
                    visible: false,
                }
            ],
        });
        $('#assignment-codes-table_filter').find("input").addClass('form-control').attr("placeholder", "Search");
        $('#assignment-codes-table tbody').on('click', 'tr', function () {
            const data = table.row(this).data();
            window.location = "/courses/${content.courseId}/assignments/" + data[6] + "/codes/" + data[5]
        });
    });
</script>
<div class="row">
    <div class="col-12">
        <div class="card my-4">
            <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                <div class="bg-gradient-primary shadow-primary border-radius-lg pt-4 pb-3">
                    <h6 class="text-white text-capitalize ps-3" style="display:inline-block;">Assignment Codes</h6>
                </div>
            </div>
            <div class="card-body px-0 pb-2">
                <div class="table-responsive p-2">
                    <table class="table mb-0" id="assignment-codes-table">
                        <thead>
                        <tr>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Language
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Is Primary
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Has Starter Code
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Has Solution Code
                            </th>
                            <th class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2">
                                Is Injectable
                            </th>
                            <th class="text-secondary opacity-7"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list content.codes as code>
                            <tr>
                                <td>${code.language}</td>
                                <td>${code.primary}</td>
                                <td>${code.hasStarter}</td>
                                <td>${code.hasSolution}</td>
                                <td>${code.injectable}</td>
                                <td>${code.id}</td>
                                <td>${code.assignmentId}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

