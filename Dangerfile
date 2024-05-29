github.dismiss_out_of_range_messages

# assign pr author to assignee
if github.pr_json['assignee'] == nil
  # skip if pr author is bot
  if github.pr_author.include?("[bot]")
    return
  end
  # assign pr author to assignee
  github.api.add_assignees(
    github.pr_json['base']['repo']['full_name'],
    github.pr_json['number'],
    [github.pr_author]
  )
end

# checkstyle
checkstyle_format.base_path = Dir.pwd
Dir.glob("**/build/reports/**/*.xml") do |file|
  checkstyle_format.report file
end
