addSbtPlugin("com.typesafe.sbt" % "sbt-s3" % "0.8")

resolvers += Resolver.url("hmrc-sbt-plugin-releases",
  url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-git-stamp" % "4.5.0")

addSbtPlugin("com.joescii" % "sbt-jasmine-plugin" % "1.3.0")
