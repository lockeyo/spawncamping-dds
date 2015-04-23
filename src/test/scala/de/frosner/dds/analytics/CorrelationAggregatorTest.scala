package de.frosner.dds.analytics

import org.scalatest.{Matchers, FlatSpec}

class CorrelationAggregatorTest extends FlatSpec with Matchers {

  val epsilon = 0.000001

  "A correlation aggregator" should "be initialized properly" in {
    val agg = new CorrelationAggregator(5)
    agg.count shouldBe 0
    agg.sums.toList shouldBe List(0d,0d,0d,0d,0d)
    agg.sumsOfSquares.toList shouldBe List(0d,0d,0d,0d,0d)
    agg.sumsOfPairs.toMap shouldBe Map(
      (0, 1) -> 0d,
      (0, 2) -> 0d,
      (0, 3) -> 0d,
      (0, 4) -> 0d,
      (1, 2) -> 0d,
      (1, 3) -> 0d,
      (1, 4) -> 0d,
      (2, 3) -> 0d,
      (2, 4) -> 0d,
      (3, 4) -> 0d
    )
  }

  it should "compute the correct sums" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(1d,2d,3d))
    agg.sums.toList shouldBe List(1d,2d,3d)
    agg.iterate(List(1d,2d,3d))
    agg.sums.toList shouldBe List(2d,4d,6d)
  }

  it should "compute the correct sums of squares" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(1d,2d,3d))
    agg.sumsOfSquares.toList shouldBe List(1d,4d,9d)
    agg.iterate(List(1d,2d,3d))
    agg.sumsOfSquares.toList shouldBe List(2d,8d,18d)
  }

  it should "compute the correct count" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(1d,2d,3d))
    agg.count shouldBe 1
    agg.iterate(List(1d,2d,3d))
    agg.count shouldBe 2
  }

  it should "compute the correct sum of pair for two columns" in {
    val agg = new CorrelationAggregator(2)
    agg.iterate(List(2d, 3d))
    agg.sumsOfPairs.toMap shouldBe Map((0, 1) -> 6d)
    agg.iterate(List(1d, 1d))
    agg.sumsOfPairs.toMap shouldBe Map((0, 1) -> 7d)
  }

  it should "compute the correct sum of pair for more than two columns" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(2d, 3d, 4d))
    agg.sumsOfPairs.toMap shouldBe Map((0, 1) -> 6d, (0, 2) -> 8d, (1, 2) -> 12d)
    agg.iterate(List(1d, 1d, 1d))
    agg.sumsOfPairs.toMap shouldBe Map((0, 1) -> 7d, (0, 2) -> 9d, (1, 2) -> 13d)
  }

  it should "merge two sums correctly" in {
    val agg1 = new CorrelationAggregator(3)
    agg1.iterate(List(1d,2d,3d))
    val agg2 = new CorrelationAggregator(3)
    agg2.iterate(List(1d,2d,3d))
    agg1.merge(agg2).sums.toList shouldBe List(2d,4d,6d)
  }

  it should "merge two sums of squares correctly" in {
    val agg1 = new CorrelationAggregator(3)
    agg1.iterate(List(1d,2d,3d))
    val agg2 = new CorrelationAggregator(3)
    agg2.iterate(List(1d,2d,3d))
    agg1.merge(agg2).sumsOfSquares.toList shouldBe List(2d,8d,18d)
  }

  it should "merge two counts correctly" in {
    val agg1 = new CorrelationAggregator(3)
    agg1.iterate(List(1d,2d,3d))
    val agg2 = new CorrelationAggregator(3)
    agg2.iterate(List(1d,2d,3d))
    agg1.merge(agg2).count shouldBe 2
  }

  it should "merge two sums of pairs correctly for two columns" in {
    val agg1 = new CorrelationAggregator(2)
    agg1.iterate(List(2d, 3d))
    val agg2 = new CorrelationAggregator(2)
    agg2.iterate(List(1d, 1d))
    agg1.merge(agg2).sumsOfPairs.toMap shouldBe Map((0, 1) -> 7d)
  }

  it should "merge two sums of pairs correctly for more than two columns" in {
    val agg1 = new CorrelationAggregator(3)
    agg1.iterate(List(2d, 3d, 4d))
    val agg2 = new CorrelationAggregator(3)
    agg2.iterate(List(1d, 1d, 1d))
    agg1.merge(agg2).sumsOfPairs.toMap shouldBe Map((0, 1) -> 7d, (0, 2) -> 9d, (1, 2) -> 13d)
  }

  it should "require the number of columns in an iteration its initial size" in {
    val agg = new CorrelationAggregator(2)
    intercept[IllegalArgumentException] {
      agg.iterate(List(1))
    }
  }

  it should "require the intermediate aggregator to have the same size when merging" in {
    val agg = new CorrelationAggregator(2)
    val intermediateAgg = new CorrelationAggregator(3)
    intercept[IllegalArgumentException] {
      agg.merge(intermediateAgg)
    }
  }

  "A correlation matrix built from a correlation aggregator" should "have 1 in the diagonal" in {
    val agg = new CorrelationAggregator(5)
    agg.iterate(List(1d, 2d, 3d, 4d, 5d))
    agg.iterate(List(5d, 3d, 2d, 1d, 4d))
    val result = agg.pearsonCorrelations
    result((0,0)) shouldBe 1d
    result((1,1)) shouldBe 1d
    result((2,2)) shouldBe 1d
    result((3,3)) shouldBe 1d
    result((4,4)) shouldBe 1d
  }

  it should "be symmetric" in {
    val agg = new CorrelationAggregator(5)
    agg.iterate(List(1d, 2d, 3d, 4d, 5d))
    agg.iterate(List(5d, 3d, 2d, 1d, 4d))
    val result = agg.pearsonCorrelations
    for (i <- 0 to 4; j <- 0 to 4) {
      result((i, j)) shouldBe result((j, i))
    }
  }

  it should "have correlation of 1 for a perfect correlated set of columns" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(1d, 2d, 3d))
    agg.iterate(List(4d, 5d, 6d))
    agg.iterate(List(7d, 8d, 9d))
    val result = agg.pearsonCorrelations
    result((0, 1)) should be (1d +- epsilon)
    result((0, 2)) should be (1d +- epsilon)
    result((1, 2)) should be (1d +- epsilon)
  }

  it should "have correlation of -1 for a perfect negatively correlated set of columns" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(1d, 8d, 3d))
    agg.iterate(List(4d, 5d, 6d))
    agg.iterate(List(7d, 2d, 9d))
    val result = agg.pearsonCorrelations
    result((0, 1)) should be (-1d +- epsilon)
    result((0, 2)) should be (1d +- epsilon)
    result((1, 2)) should be (-1d +- epsilon)
  }

  it should "give the same correlation values as R for a normal data set" in {
    val agg = new CorrelationAggregator(3)
    agg.iterate(List(5d, 7d, 1d))
    agg.iterate(List(3d, 2d, 8d))
    agg.iterate(List(2d, 6d, -5d))
    val result = agg.pearsonCorrelations
    result((0, 1)) should be (0.3711537 +- epsilon)
    result((0, 2)) should be (0.2850809 +- epsilon)
    result((1, 2)) should be (-0.7842301 +- epsilon)
  }

}
