/**
 * @name Extraction errors
 * @description List all extraction errors for files in the source code directory.
 * @kind diagnostic
 * @id js/diagnostics/extraction-errors
 */

import javascript

/** Gets the SARIF severity to associate to an extractor error. */
int getSeverity() { result = 1 }

from Error error
where
  exists(error.getFile().getRelativePath()) and
  error.isFatal()
select error, "Extraction failed in " + error.getFile() + " with error " + error.getMessage(),
  getSeverity()
