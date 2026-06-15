import { getRatioStatus } from "./ratioCalculations";

// Pre-defined questions for credit assessment
export const creditQuestions = [
  {
    id: 1,
    category: "Business Profile",
    question: "How many years has the business been in operation?",
    options: [
      { label: "Less than 2 years", score: 1 },
      { label: "2-5 years", score: 2 },
      { label: "5-10 years", score: 3 },
      { label: "More than 10 years", score: 4 },
    ],
  },
  {
    id: 2,
    category: "Business Profile",
    question: "What is the company's market position in its industry?",
    options: [
      { label: "New entrant / Marginal player", score: 1 },
      { label: "Small player with some market presence", score: 2 },
      { label: "Established mid-size player", score: 3 },
      { label: "Market leader / Top-tier player", score: 4 },
    ],
  },
  {
    id: 3,
    category: "Management Quality",
    question: "How would you rate the management team's experience and track record?",
    options: [
      { label: "Limited experience, no proven track record", score: 1 },
      { label: "Some experience with moderate track record", score: 2 },
      { label: "Experienced team with good track record", score: 3 },
      { label: "Highly experienced with excellent track record", score: 4 },
    ],
  },
  {
    id: 4,
    category: "Management Quality",
    question: "Does the company have a succession plan in place?",
    options: [
      { label: "No succession plan", score: 1 },
      { label: "Informal plan discussed", score: 2 },
      { label: "Documented plan in place", score: 3 },
      { label: "Well-tested and documented plan", score: 4 },
    ],
  },
  {
    id: 5,
    category: "Industry Risk",
    question: "What is the current industry outlook?",
    options: [
      { label: "Declining industry with high volatility", score: 1 },
      { label: "Stable but with competitive pressures", score: 2 },
      { label: "Growing moderately with manageable risks", score: 3 },
      { label: "Strong growth with favorable dynamics", score: 4 },
    ],
  },
  {
    id: 6,
    category: "Industry Risk",
    question: "How diversified is the company's revenue base?",
    options: [
      { label: "Single product/customer dependency (>70%)", score: 1 },
      { label: "Moderate concentration (40-70%)", score: 2 },
      { label: "Diversified with some concentration (20-40%)", score: 3 },
      { label: "Well-diversified across products/customers", score: 4 },
    ],
  },
  {
    id: 7,
    category: "Credit History",
    question: "What is the borrower's repayment history?",
    options: [
      { label: "Defaults or significant delinquencies", score: 1 },
      { label: "Occasional late payments", score: 2 },
      { label: "Generally on-time with rare delays", score: 3 },
      { label: "Perfect repayment record", score: 4 },
    ],
  },
  {
    id: 8,
    category: "Credit History",
    question: "How is the relationship history with the lending institution?",
    options: [
      { label: "New customer, no history", score: 1 },
      { label: "Short relationship (<2 years)", score: 2 },
      { label: "Established relationship (2-5 years)", score: 3 },
      { label: "Long-standing relationship (>5 years)", score: 4 },
    ],
  },
  {
    id: 9,
    category: "Collateral Quality",
    question: "What is the quality and liquidity of the collateral offered?",
    options: [
      { label: "Illiquid or depreciating assets", score: 1 },
      { label: "Moderately liquid assets", score: 2 },
      { label: "Quality assets with good marketability", score: 3 },
      { label: "Highly liquid, prime assets", score: 4 },
    ],
  },
  {
    id: 10,
    category: "Collateral Quality",
    question: "Is the collateral adequately insured and free of encumbrances?",
    options: [
      { label: "Uninsured with existing liens", score: 1 },
      { label: "Partially insured, some encumbrances", score: 2 },
      { label: "Fully insured with minor encumbrances", score: 3 },
      { label: "Fully insured and unencumbered", score: 4 },
    ],
  },
];

// Calculate score from financial ratios (max 60 points)
export function calculateRatioScore(ratios) {
  let score = 0;
  const maxScore = 60;
  const pointsPerRatio = maxScore / ratios.length; // 5 points each

  ratios.forEach((ratio) => {
    const status = getRatioStatus(ratio);
    switch (status) {
      case "good":
        score += pointsPerRatio;
        break;
      case "fair":
        score += pointsPerRatio * 0.6;
        break;
      case "poor":
        score += pointsPerRatio * 0.2;
        break;
    }
  });

  return Math.round(score);
}

// Calculate score from questionnaire (max 40 points)
export function calculateQuestionnaireScore(answers) {
  const maxPossible = creditQuestions.length * 4; // max 4 per question
  let totalScore = 0;

  Object.values(answers).forEach((score) => {
    totalScore += score;
  });

  // Normalize to 40 points
  return Math.round((totalScore / maxPossible) * 40);
}

// Derive final credit score and rating
export function deriveCreditScore(ratioScore, questionnaireScore) {
  const totalScore = ratioScore + questionnaireScore; // max 100

  let rating, ratingDescription, color;

  if (totalScore >= 85) {
    rating = "AAA";
    ratingDescription = "Exceptional creditworthiness - Lowest risk";
    color = "#10b981";
  } else if (totalScore >= 75) {
    rating = "AA";
    ratingDescription = "Very high creditworthiness - Very low risk";
    color = "#34d399";
  } else if (totalScore >= 65) {
    rating = "A";
    ratingDescription = "High creditworthiness - Low risk";
    color = "#6ee7b7";
  } else if (totalScore >= 55) {
    rating = "BBB";
    ratingDescription = "Good creditworthiness - Moderate risk";
    color = "#fbbf24";
  } else if (totalScore >= 45) {
    rating = "BB";
    ratingDescription = "Adequate creditworthiness - Moderate-high risk";
    color = "#f59e0b";
  } else if (totalScore >= 35) {
    rating = "B";
    ratingDescription = "Below average creditworthiness - High risk";
    color = "#f97316";
  } else if (totalScore >= 25) {
    rating = "CCC";
    ratingDescription = "Weak creditworthiness - Very high risk";
    color = "#ef4444";
  } else {
    rating = "D";
    ratingDescription = "Default / Extremely high risk";
    color = "#991b1b";
  }

  return {
    totalScore,
    ratioScore,
    questionnaireScore,
    rating,
    ratingDescription,
    color,
  };
}
